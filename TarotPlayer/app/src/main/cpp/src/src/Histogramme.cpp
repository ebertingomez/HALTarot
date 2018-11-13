#include "Histogramme.hpp"

#define MINIATURE_HAUTEUR 50
#define MINIATURE_LARGEUR_PETIT 50
#define MINIATURE_LARGEUR_HONNEUR 35
#define MINIATURE_LARGEUR (honneur ? MINIATURE_LARGEUR_HONNEUR : MINIATURE_LARGEUR_PETIT )
#define MASQUE(image) (honneur ? (image)(Range::all(), Range(MINIATURE_LARGEUR_PETIT - MINIATURE_LARGEUR_HONNEUR,MINIATURE_HAUTEUR)) : (image))

using namespace std;
using namespace cv;

Cache* Histogramme::cache = NULL;

Histogramme::Histogramme(string const& fichier): code("--"), note_total(0), rapport_rouge(0)
{
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
        cout << "erreur de lecture du fichier " << fichier << endl;
        code[0] = '!';code[1] = '!';
    }
}

Histogramme::Histogramme(Mat const image, string code): code(code), note_total(0), rapport_rouge(0), image(image)
{
    if (!image.data || image.channels() != 3)
    {
        cout << "ERREUR : objet Histomgramme initialisé avec matrice non valide " << endl;
        code = "!!";
    }
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
				valeur[k]		+= histo.at<float>(i,j,k);
				valeur_glob[k]	+= histo.at<float>(i,j,k);
				saturation[j]	+= histo.at<float>(i,j,k);
			}
		}
        for ( j = 5 ; j < 20 ; j ++ )
        {
			for ( i = 18 ; i < 22 ; i ++ )
			{
				saturation[j]	+= histo.at<float>(i%20,j,k);
				rapport_rouge	+= histo.at<float>(i%20,j,k);
				valeur_glob[k]	+= histo.at<float>(i%20,j,k);
				teinte[i]		+= histo.at<float>(i%20,j,k);
			}
			for ( i = 2 ; i < 18 ; i ++ )
			{
				teinte[i]		+= histo.at<float>(i,j,k);
				saturation[j]	+= histo.at<float>(i,j,k);
				valeur_glob[k]	+= histo.at<float>(i,j,k);
			}
		}
	}
	float poids = 0;
	for ( i = 0 ; i < 20 ; i ++ ) poids += teinte[i];
	for ( i = 0 ; i < 20 ; i ++ ) teinte[i] *= 10000.0 / poids;
}


bool Histogramme::maxima(unsigned int* valeur, int& max, int& second)
{
	max = -1; second = -1;
	for ( int i = 0 ; i < 20 ; i ++ )
	{
		if ( i == 0  && valeur [0] < valeur[1]  ) continue;
		if ( i == 19 && valeur[19] < valeur[18] ) continue;
		if ( valeur[i] < valeur[i+1] || valeur[i] < valeur[i-1] ) continue;

		if ( max == -1 ) max = i;
		else if ( valeur[i] < valeur[max] && max - i > 3)
		{
			if ( second == -1 ) second = i;
			else if ( valeur[second] < valeur[i]) second = i;
		}
		else
		{
			if ( i - max > 3 ) second = max;
			max = i;
		}
	}


	if ( max == -1 || second == -1 )			return false;
	if ( valeur[second] * 200 < valeur[max] )	return false;
	if ( ABS( max - second ) < 4 )				return false;
	return true;
}

struct dkfjhgnkjhg { bool operator() ( vector<Point> i , vector<Point> j ) { return contourArea(i) < contourArea(j); } } compare_aire;

Classification Histogramme::classification_couleur(Mat const& image, bool rouge, bool honneur, int* nombre_match)
{
	vector<vector<Point>> contours;
	vector<Vec4i> topologie;
	Mat miniature(MINIATURE_HAUTEUR, MINIATURE_LARGEUR,CV_32F), affine(2,3,CV_32S);
	Mat pre_miniature, pre_miniaturef;
	Point2f original[3];
	Rect rectangle;
	const Point2f dimensions_miniature[3] = { Point(MINIATURE_LARGEUR, 0) , Point(0,0) , Point(0, MINIATURE_HAUTEUR) };

	Mat image_binaire = image.clone();
	findContours(image_binaire, contours, topologie, RETR_TREE, CHAIN_APPROX_TC89_L1);

	Classification sortie = AUTRE;

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
			cout << "distance coeur : "		<< norm_coeur << endl;
			cout << "distance carreau : "	<< norm_carreau << endl;
			#endif

			flip(miniature, miniature, 0);

			norm_coeur		= min( norm_coeur,	(int) norm(MASQUE(cache->getCoeur()),		255 * miniature));

			#ifdef DEBUG_
			cout << "distance coeur : "		<< norm_coeur << endl;
			cout << "distance carreau : "	<< norm_carreau << endl;
			#endif

			if ( honneur ) return (norm_coeur > norm_carreau) ? HONNEUR_CARREAU : HONNEUR_COEUR;
			if ( norm_coeur < norm_carreau and norm_coeur	< 4000 ) sortie = PETIT_COEUR;
			if ( norm_coeur > norm_carreau and norm_carreau	< 4000 ) sortie = PETIT_CARREAU;
		}
		else
		{
			Mat test = MASQUE(cache->getPique());

			int norm_pique		= norm(MASQUE(cache->getPique()),		miniature);
			int norm_pissenlit	= norm(MASQUE(cache->getPissenlit()),	miniature);

			#ifdef DEBUG_
			cout << "distance pique : "		<< norm_pique << endl;
			cout << "distance trèfle : "	<< norm_pissenlit << endl;
			#endif

			flip(miniature, miniature, 0);

			norm_pique		= min( norm_pique,		(int) norm(MASQUE(cache->getPique()),		miniature));
			norm_pissenlit	= min( norm_pissenlit,	(int) norm(MASQUE(cache->getPissenlit()),	miniature));


			#ifdef DEBUG_
			cout << "distance pique : "		<< norm_pique << endl;
			cout << "distance trèfle : "	<< norm_pissenlit << endl;
			#endif

			if ( honneur ) return (norm_pique < norm_pissenlit) ? HONNEUR_PIQUE : HONNEUR_TREFLE;
			if ( norm_pique < norm_pissenlit and norm_pique		< 4000 ) sortie = PETIT_PIQUE;
			if ( norm_pique > norm_pissenlit and norm_pissenlit	< 4000 ) sortie = PETIT_TREFLE;
		}
	}
	return sortie;
}

Mat* isole_rouge(Mat image)
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
			if ( ptr[3*j+1] > 53 && ( ptr[3*j] < 13 || ptr[3*j] > 230 )) ptr_rouge[j] = 1;
			else ptr_rouge[j] = 0;
		}
	}
	return rouge;
}

void somme_cumule(unsigned int* valeur) { for ( int i = 1 ; i < 20 ; i ++ ) valeur[i] += valeur[i-1]; }

Classification Histogramme::classification_neuronale(bool honneur)
{
	float entre[125];
	vector<float> sortie(4);

    for (int i = 0; i < 125; i++)
	{
		entre[i] = (float)somme_cube(histo,i)/note_total;
		cout << entre[i] << ",";
	}
	if ( not honneur )	cache->neurones_carte->predict(vector<float>(entre, entre+125), sortie);
    else 				cache->neurones_coin ->predict(vector<float>(entre, entre+125), sortie);

	#ifdef DEBUG_
    cout << endl << "résultats du réseau de neurone :    autre : " 	<< sortie[0] <<  " ---- noir : "
																	<< sortie[1] << " ----   rouge : "
																	<< sortie[2] << " ----- rien : "
																	<< sortie[3] << endl;
    #endif

	if ( sortie[1] < -0.7 or sortie[2] < -0.7 or sortie[1] < -0.7 ) return RIEN;
	if ( honneur )
	{
		if ( sortie[1] > 0.7 ) return HONNEUR_NOIR;
		if ( sortie[2] > 0.7 ) return HONNEUR_ROUGE;
		if ( sortie[0] > 0.7 ) return ATOUT;
	}
	else
	{
		if ( sortie[1] > 0.7 ) return PETITE_NOIRE;
		if ( sortie[2] > 0.7 ) return PETITE_ROUGE;
		if ( sortie[0] > 0.7 ) return AUTRE;
	}

    return RIEN;
}

Classification Histogramme::classification_carte(int* hauteur_carte)
{
	Classification classe = classification_neuronale(hauteur_carte == 0);

	int max, second;

	bool noir_possible = maxima(valeur, max, second);
	int milieu_noir = (max + second) / 2;
	somme_cumule(valeur);

	switch (classe)
	{

	case PETITE_NOIRE:
	case HONNEUR_NOIR:
	{
		Mat image_binaire;
		extractChannel(image, image_binaire, 2);
		compare(image_binaire, Scalar(milieu_noir*256/20), image_binaire, CMP_LE );

		return classification_couleur(image_binaire, false, hauteur_carte == 0, hauteur_carte);
	}

	case PETITE_ROUGE:
	case HONNEUR_ROUGE:
	{
		bool rouge_possible = maxima(&(saturation[0]), max, second);
		int milieu_rouge = (max + second) / 2;
		Mat* image_binaire = isole_rouge(image);

		classe = classification_couleur(*image_binaire, true, hauteur_carte == 0, hauteur_carte);
		delete image_binaire;

		return classe;
	}

	case RIEN:
		return RIEN;

	default:
		return (hauteur_carte == 0)? ATOUT : AUTRE;
	}
}

