#include "Histogramme.hpp"

#define MINIATURE_HAUTEUR 50
#define MINIATURE_LARGEUR_PETIT 50
#define MINIATURE_LARGEUR_HONNEUR 35
#define MINIATURE_LARGEUR (honneur ? MINIATURE_LARGEUR_HONNEUR : MINIATURE_LARGEUR_PETIT )
#define MASQUE(image) (honneur ? (image)(Range::all(), Range(MINIATURE_LARGEUR_PETIT - MINIATURE_LARGEUR_HONNEUR,MINIATURE_HAUTEUR)) : (image))

using namespace std;
using namespace cv;

extern string chemin_absolu; // TODO: to remove
Cache* Histogramme::cache = NULL;

Histogramme::Histogramme(string const& fichier): code("--"), note_total(0), rapport_rouge(0)
{
	ofstream log (chemin_absolu + "log.txt");
	image = imread(fichier,1);
    if (image.data)
    {
        cvtColor(image,image,CV_BGR2HSV);
        int nom_fichier = fichier.find_last_of('/');
		code = fichier.substr(nom_fichier+1, 2);
		note_total = image.cols*image.rows;
    }
    else
    {
        log << "erreur de lecture du fichier " << fichier << endl;
        code[0] = '!';code[1] = '!';
    }
    log.close();
}

Histogramme::Histogramme(Mat const image, string code): code(code), note_total(0), rapport_rouge(0), image(image)
{
	ofstream log (chemin_absolu + "log.txt");
	if (!image.data || image.channels() != 3)
    {
		log << "ERREUR : objet Histomgramme initialisé avec matrice non valide" << endl;
		log << "data : " << image.data << "   -------  channels : " << image.channels() << endl;
        code = "!!";
    }
    log.close();
    note_total = image.cols*image.rows;
}

Histogramme::Histogramme(string code, bool ne_sert_a_rien) : code(code), note_total(0), rapport_rouge(0)
{}

float Histogramme::distance_teinte(Histogramme const& autre)
{
	float somme = 0;
	for ( int i = 0 ; i < 20 ; i ++ ) somme += (teinte[i]-autre.getTeinte(i))*(teinte[i]-autre.getTeinte(i));
	return somme;
}

void Histogramme::histogramme()
{
    float s_ranges[] = { 0, 256 };
	int channels[] = { 0, 1, 2 };
	int histSize[] = { 20, 20, 20 };

	float h_ranges[] = { 0, 180 };
	float v_ranges[] = { 0, 256 };
	const float* ranges[] = { h_ranges, s_ranges, v_ranges };

	calcHist( &image, 1, channels, Mat(), histo, 3, histSize, ranges, true, false );

	char i, j, k;

	for ( k = 0 ; k < 20 ; k ++ )
    {
        for ( j = 0 ; j < 5 ; j ++ )
        {
			for ( i = 0 ; i < 20 ; i ++ )
			{
				valeur[k]	+= histo.at<float>(i,j,k);
				saturation[j]	+= histo.at<float>(i,j,k);
			}
		}
        for ( j = 5 ; j < 20 ; j ++ )
        {
			for ( i = 18 ; i < 22 ; i ++ )
			{
				saturation[j]	+= histo.at<float>(i%20,j,k);
				rapport_rouge	+= histo.at<float>(i%20,j,k);
				valeur[k]		+= histo.at<float>(i%20,j,k);
				teinte[i]		+= histo.at<float>(i%20,j,k);
			}
			for ( i = 2 ; i < 18 ; i ++ )
			{
				teinte[i]		+= histo.at<float>(i,j,k);
				saturation[j]	+= histo.at<float>(i,j,k);
				valeur[k]		+= histo.at<float>(i,j,k);
			}
		}
	}
	float poids = 0;
	for ( i = 0 ; i < 20 ; i ++ ) poids += teinte[i];
	for ( i = 0 ; i < 20 ; i ++ ) teinte[i] *= 10000.0 / poids;
}

/*
 * Soit un 20-uplet. La fonction suivante recherche dans ce 20-uplet l'indice du maximum et celui du deuxième plus grand maximum local.
 * Par exemple, si "valeur" est l'histogramme en saturation d'un 8 de carreau. Cet histogramme présentera deux maximum locaux bien distincts, correspondant à l'aire blanche
 * et à l'aire rouge de la carte.
 * Cette fonction renvoie vrai si de tels maxima locaux existent, sont suffisamment distincts, et dont l'intensité est suffisamment proche en ordre de randeur.
 */

bool Histogramme::maxima(unsigned int* valeur, int& max, int& second)
{
	max = -1; second = -1;
	for ( int i = 0 ; i < 20 ; i ++ )
	{
		if ( i == 0 and valeur [0] <= valeur[1]  ) continue;
		if ( i == 19 and valeur[19] <= valeur[18] ) continue;
		if ( i != 0 and i != 19 and (valeur[i] < valeur[i+1] or valeur[i] < valeur[i-1] )) continue;

		if ( max == -1 ) max = i;
		else if ( valeur[i] < valeur[max] && i - max > 3)
		{
			if ( second == -1 ) second = i;
			else if ( valeur[second] < valeur[i]) second = i;
		}
		else if ( valeur[i] > valeur[max])
		{
			 if ( i - max > 3 )
				second = max;
			max = i;
		}
	}


	if ( max == -1 || second == -1 )			return false;
	if ( valeur[second] * 200 < valeur[max] )	return false;
	if ( ABS( max - second ) < 4 )				return false;
	return true;
}

struct dkfjhgnkjhg { bool operator() ( vector<Point> i , vector<Point> j ) { return contourArea(i) < contourArea(j); } } compare_aire;

extern int plafond_ressemblance_couleur;

/*
 * Cette fonction recherche la couleur d'une carte. Elle prend en paramètre une image binaire, sur laquelle elle recherche les piques, les carreaux ... etc.
 * Elle identifie ces motifs par superposition avec les quatres couleurs possibles.
 *
 */

Classification Histogramme::classification_couleur(Mat const& image, bool rouge, bool honneur, int* nombre_match)
{
	vector<vector<Point>> contours;
	vector<Vec4i> topologie;
	Mat miniature(MINIATURE_HAUTEUR, MINIATURE_LARGEUR,CV_32F), affine(2,3,CV_32S);
	Mat pre_miniature, pre_miniaturef;
	Point2f original[3];
	Rect rectangle;
	static Point2f dimensions_miniature[3] = { Point(MINIATURE_LARGEUR, 0) , Point(0,0) , Point(0, MINIATURE_HAUTEUR) };
	dimensions_miniature[0].x = MINIATURE_LARGEUR;

	Mat image_binaire = image.clone();

	findContours(image_binaire, contours, topologie, RETR_TREE, CHAIN_APPROX_TC89_L1);

	Classification sortie = AUTRE;

	ofstream log (chemin_absolu + "log.txt");

	for ( int erjhj = 0 ; erjhj < contours.size() ; erjhj ++ )
	{

		if ( honneur )
		{
			rectangle = boundingRect(*max_element(contours.begin(), contours.end(), compare_aire));
		}
		else
		{
			if ( contourArea(contours[erjhj]) * 110 < note_total ) continue;
			rectangle = boundingRect(contours[erjhj]);
			nombre_match[0] ++;
			if ( sortie != AUTRE ) continue;
		}

		original[0] = Point(rectangle.width, 0);
		original[1] = Point(0, 0);
		original[2] = Point(0, rectangle.height);

		affine = getAffineTransform(original, dimensions_miniature);
		pre_miniature = image(Range(rectangle.y, rectangle.y + rectangle.height), Range(rectangle.x, rectangle.x + rectangle.width));

		pre_miniature.convertTo(pre_miniaturef, CV_32F);
		warpAffine(pre_miniaturef, miniature, affine, miniature.size());		miniature.convertTo(miniature, CV_8U);

		if ( rouge )
		{
			int norm_coeur		= norm(MASQUE(cache->getCoeur()),		255 * miniature);
			int norm_carreau	= norm(MASQUE(cache->getCarreau()),		255 * miniature);

			#ifdef DEBUG_
			log << "distance coeur : "		<< norm_coeur << "     -------------       distance carreau : "	<< norm_carreau << endl;
			#endif

			flip(miniature, miniature, 0);

			norm_coeur		= min( norm_coeur,	(int) norm(MASQUE(cache->getCoeur()),	255 * miniature));

			#ifdef DEBUG_
			log << "distance coeur : "		<< norm_coeur << "     -------------       distance carreau : "	<< norm_carreau << endl;
			#endif

			if ( norm_coeur < norm_carreau and norm_coeur	< plafond_ressemblance_couleur ) sortie = honneur ? HONNEUR_COEUR : PETIT_COEUR;
			if ( norm_coeur > norm_carreau and norm_carreau	< plafond_ressemblance_couleur ) sortie = honneur ? HONNEUR_CARREAU : PETIT_CARREAU;
		}
		else
		{
			int norm_pique		= norm(MASQUE(cache->getPique()),		miniature);
			int norm_pissenlit	= norm(MASQUE(cache->getPissenlit()),	miniature);

			#ifdef DEBUG_
			log << "distance pique : "		<< norm_pique <<  "     -------------       distance trèfle : "	<< norm_pissenlit << endl;
			#endif

			flip(miniature, miniature, 0);

			norm_pique		= min( norm_pique,		(int) norm(MASQUE(cache->getPique()),		miniature));
			norm_pissenlit	= min( norm_pissenlit,	(int) norm(MASQUE(cache->getPissenlit()),	miniature));


			#ifdef DEBUG_
			log << "distance pique : "		<< norm_pique <<  "     -------------       distance trèfle : "	<< norm_pissenlit << endl;
			#endif

			if ( norm_pique < norm_pissenlit and norm_pique		< plafond_ressemblance_couleur ) sortie = honneur ? HONNEUR_PIQUE : PETIT_PIQUE;
			if ( norm_pique > norm_pissenlit and norm_pissenlit	< plafond_ressemblance_couleur ) sortie = honneur ? HONNEUR_TREFLE : PETIT_TREFLE;
		}
		if (honneur) return sortie;
	}
	log.close();
	return sortie;
}

Mat* isole_rouge(Mat image, int milieu)									// construit l'image binaire d'une carte rouge
{
	Mat* rouge = new Mat(image.rows,image.cols, CV_8U);
	int lignes = image.rows;
	int colonnes = image.cols;
	uchar* ptr;
	uchar* ptr_rouge;

	if ( image.isContinuous() && rouge->isContinuous() )
	{
		colonnes *= lignes;
		lignes = 1;
	}
	for ( int i = 0 ; i < lignes ; i ++ )
	{
		ptr = image.ptr<uchar>(i);
		ptr_rouge = rouge->ptr<uchar>(i);
		for ( int j = 0 ; j < colonnes ; j ++ )
		{
			if ( ptr[3*j+1] > milieu*13 && ( ptr[3*j] < 13 || ptr[3*j] > 160 )) ptr_rouge[j] = 1;
			else ptr_rouge[j] = 0;
		}
	}
	return rouge;
}

void somme_cumule(unsigned int* valeur) { for ( int i = 1 ; i < 20 ; i ++ ) valeur[i] += valeur[i-1]; }

/*
 *
 * Utilise des arbres boosté (anciennement des réseau neuronaux) pour déterminer la nature d'une carte :
 * - petite rouge
 * - petite noire
 * - autre (atout et honneur)
 *
 */

Classification Histogramme::classification_neuronale()
{
	static const Ptr<Boost> arbre_type		= Boost::load("data/arbre_type_carte");
    static const Ptr<Boost> arbre_couleur	= Boost::load("data/arbre_couleur_carte");

	ofstream log (chemin_absolu + "log.txt");

	vector<float> entre(85);
	vector<float> sortie(1);

	float normalise = 1000.0/note_total;

    for (int i = 0; i < 25; i++)
		entre[i] 	= (float)somme_cube(histo,i)*normalise;
	for (int i = 25; i < 35; i++)
		entre[i-25] = (float)somme_cube(histo,i)*normalise;
	for (int i = 35; i < 50; i++)
		entre[i-10] = (float)somme_cube(histo,i)*normalise;
	for (int i = 50; i < 60; i++)
		entre[i-50] = (float)somme_cube(histo,i)*normalise;
	for (int i = 60; i < 75; i++)
		entre[i-20] = (float)somme_cube(histo,i)*normalise;
	for (int i = 75; i < 85; i++)
		entre[i-75] = (float)somme_cube(histo,i)*normalise;
	for (int i = 85; i < 100; i++)
		entre[i-30] = (float)somme_cube(histo,i)*normalise;
	for (int i = 100; i < 110; i++)
		entre[i-100] =(float)somme_cube(histo,i)*normalise;
	for (int i = 110; i < 125; i++)
		entre[i-40] = (float)somme_cube(histo,i)*normalise;


	for (int i = 0; i < 85; i++)
		log << entre[i] << "," ;

	log.close(); //TODO: delete
	arbre_type->predict(entre, sortie);
	if ( sortie[0] == -1 )
	{
		arbre_couleur->predict(entre, sortie);
		if ( sortie[0] == 1 )
			return PETITE_NOIRE;
		else
			return PETITE_ROUGE;
	}
	else
		return AUTRE;
}

Classification Histogramme::classification_carte(int* hauteur_carte)
{
	Classification classe;

	int max, second;

	bool noir_possible, rouge_possible;
	int milieu_rouge, milieu_noir;
	Mat* image_binaire_rouge;
	Mat image_binaire_noire;

	if (rouge_possible = 	maxima(saturation, max, second))
		milieu_rouge = (max + second) / 2;

	if ( noir_possible =	maxima(valeur, max, second))
		milieu_noir = (max + second) / 2;

	if ( rouge_possible and noir_possible )								// différentie carte rouge et noire, en comparant la variance des histogramme S et V.
	{
		float moyenne_rouge(0), moyenne_noire(0), moyenne2_rouge(0), moyenne2_noire(0), total_rouge(0), total_noire(0);
		for (int i = 0; i < 20; i++)
		{
			total_rouge		+= 		saturation[i];
			total_noire		+=		valeur[i];
			moyenne_rouge 	+= i *	saturation[i];
			moyenne2_rouge	+= i *	saturation[i] * i;
			moyenne_noire 	+= i *	valeur[i];
			moyenne2_noire	+= i *	valeur[i] * i;
		}
		if ((moyenne2_rouge - moyenne_rouge * moyenne_rouge / total_rouge) / total_rouge >
			(moyenne2_noire - moyenne_noire * moyenne_noire / total_noire) / total_noire)
				noir_possible	= false;
		else 	rouge_possible	= false;
	}

	if ( rouge_possible )
	{
		image_binaire_rouge = isole_rouge(image, milieu_rouge);
		classe = classification_couleur(*image_binaire_rouge, true, hauteur_carte == 0, hauteur_carte);
		delete image_binaire_rouge;

		if ( classe != AUTRE ) return classe;
	}

	if ( noir_possible )
	{

		extractChannel(image, image_binaire_noire, 2);
		compare(image_binaire_noire, Scalar(milieu_noir*256/20), image_binaire_noire, CMP_LE );

		classe =  classification_couleur(image_binaire_noire, false, hauteur_carte == 0, hauteur_carte);
		if ( classe != AUTRE ) return classe;
	}

	if (hauteur_carte == 0)	return ATOUT;

	return AUTRE;

}

