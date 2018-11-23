#include "annexe.hpp"
#define  DISCRIMINE_COULEUR(lettre,  petit,  honneur) switch(lettre) \
	{\
		case '0':\
		case '1':\
		case '2':\
		case '3':\
		case '4':\
		case '5':\
		case '6':\
		case '7':\
		case '8':\
		case '9':\
			return petit;\
		case 'V':\
		case 'C':\
		case 'D':\
		case 'R':\
			return honneur;\
		default:\
			return RIEN;\
	}

void somme_mat_3x3(Mat a, Mat b)
{
	double* pa;
	double* pb;
	for (int i = 0; i < 3; i++)
	{
		pa = a.ptr<double>(i);
		pb = b.ptr<double>(i);
		for (int j = 0; j < 3; j++)
			pa[j] = ( 5 * pa[j] + pb[j] ) / 6 ;
	}
}

pair<float, float> regression(vector<Vec4i> lignes)
{
	float moyenne_x = 0;
	float moyenne_y = 0;

	for (int i = 0; i < lignes.size(); i++)
	{
		moyenne_x += (float)lignes[i][0] + lignes[i][2];
		moyenne_y += (float)lignes[i][1] + lignes[i][3];
	}

	moyenne_x /= lignes.size() * 2;
	moyenne_y /= lignes.size() * 2;

	float var = 0;
	float cov = 0;
	float b = 0;

	for (int i = 0; i < lignes.size(); i++)
	{
		var += ( lignes[i][0] - moyenne_x ) * ( lignes[i][0] - moyenne_x ) + ( lignes[i][2] - moyenne_x ) * ( lignes[i][2] - moyenne_x );
		cov += ( lignes[i][0] - moyenne_x ) * ( lignes[i][1] - moyenne_y ) + ( lignes[i][2] - moyenne_x ) * ( lignes[i][3] - moyenne_y );
	}

	pair<float, float> paire;
	if ( var < 0.0001 )
	{
		paire.first		= -1;
		paire.second	= - lignes[0][0];
	}
	else
	{
		paire.first		= cov / var;
		paire.second	= moyenne_y - paire.first * moyenne_x;
	}
	return paire;
}

string transcription_francais(Classification classe)
{
	switch (classe)
	{
		case HONNEUR_COEUR:
		case PETIT_COEUR:		return "coeur";
		case HONNEUR_CARREAU:
		case PETIT_CARREAU:		return "carreau";
		case HONNEUR_PIQUE:
		case PETIT_PIQUE:		return "pique";
		case HONNEUR_TREFLE:
		case PETIT_TREFLE:		return "trèfle";
		case PETITE_NOIRE:		return "pique ou trèfle";
		case PETITE_ROUGE:		return "carreau ou coeur";
		case ATOUT:
		case AUTRE:				return "atout";
	}
	return "????";
}

Classification classification(string nom, bool simple )
{
	if ( simple )
		switch(nom[1])
		{
			case 'O':
			case 'A':
				DISCRIMINE_COULEUR(nom[0], PETITE_ROUGE, AUTRE);
			case 'T':
			case 'P':
				DISCRIMINE_COULEUR(nom[0], PETITE_NOIRE, AUTRE);
			case '0':
				if ( nom[0] == '1' || nom[0] == '2' ) return AUTRE;
			case '1':
				if ( nom[0] == '0' || nom[0] == '1' || nom[0] == '2' ) return AUTRE;
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				if ( nom[0] == '0' || nom[0] == '1' ) return AUTRE;
			case 'X':
				if ( nom[0] == 'E') return AUTRE;
		}
	else
		switch(nom[1])
		{
			case 'O':
				DISCRIMINE_COULEUR(nom[0], PETIT_COEUR, HONNEUR_COEUR);
			case 'A':
				DISCRIMINE_COULEUR(nom[0], PETIT_CARREAU, HONNEUR_CARREAU);
			case 'T':
				DISCRIMINE_COULEUR(nom[0], PETIT_TREFLE, HONNEUR_TREFLE);
			case 'P':
				DISCRIMINE_COULEUR(nom[0], PETIT_PIQUE, HONNEUR_PIQUE);
			case '0':
				if ( nom[0] == '1' || nom[0] == '2' ) return ATOUT;
			case '1':
				if ( nom[0] == '0' || nom[0] == '1' || nom[0] == '2' ) return ATOUT;
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				if ( nom[0] == '0' || nom[0] == '1' ) return ATOUT;
			case 'X':
				if ( nom[0] == 'E') return EXCUSE;
		}
	return RIEN;
}

bool vraisemblance_rectangle(Point2f* scene, int rows, int cols)
{
	double a, b, c, d;
	if ((a =	DISTANCE(scene[0], scene[1]))	< 30 ) return false;
	if ((b =	DISTANCE(scene[2], scene[1]))	< 30 ) return false;
	if ( 		DISTANCE(scene[3], scene[1])	< 30 ) return false;
	if (		DISTANCE(scene[0], scene[2])	< 30 ) return false;
	if ((d =	DISTANCE(scene[0], scene[3]))	< 30 ) return false;
	if ((c =	DISTANCE(scene[3], scene[2]))	< 30 ) return false;

	if ( rows && cols )
	{
		if ( scene[0].x < 0 || scene[1].x < 0 || scene[2].x < 0 || scene[4].x < 0 ) return false;
		if ( scene[0].y < 0 || scene[1].y < 0 || scene[2].y < 0 || scene[4].y < 0 ) return false;
		if ( scene[0].x > cols || scene[1].x > cols || scene[2].x > cols || scene[4].x > cols ) return false;
		if ( scene[0].y > rows || scene[1].y > rows || scene[2].y > rows || scene[4].y > rows ) return false;
	}


	if ( ABS( a/c - 1 ) > 0.3 ) return false;
	if ( ABS( b/d - 1 ) > 0.3 ) return false;

	#ifdef DEBUG_HOMOGRAPHIE
	double 	q = CARRE(SCALAIRE(scene[0], scene[1], scene[2]));
			q = CARRE(SCALAIRE(scene[1], scene[2], scene[3]));
			q = CARRE(SCALAIRE(scene[2], scene[3], scene[0]));
	#endif

	if ( CARRE(SCALAIRE(scene[0], scene[1], scene[2])) * 8 > a*b ) return false;
	if ( CARRE(SCALAIRE(scene[1], scene[2], scene[3])) * 8 > b*c ) return false;
	if ( CARRE(SCALAIRE(scene[2], scene[3], scene[0])) * 8 > c*d ) return false;

	return true;
}

extern Point2f* points;
extern Mat image;
extern ofstream fichier;

Point2f intersection_segment(Vec4f const& a, Vec4f const& b)
{
	float determinant_principal =  (a[1]-a[3])*(b[2]-b[0]) - (a[0]-a[2]) * (b[3]-b[1]);
	if ( ABS(determinant_principal) < 0.001 )
		return Point2f(-1, -1);
	return Point2f( 	( (b[2]-b[0]) * (a[2]*a[1] - a[0]*a[3]) + (a[0]-a[2]) * (b[2]*b[1] - b[0]*b[3])) / determinant_principal,
						( (a[1]-a[3]) * (b[2]*b[1] - b[0]*b[3]) + (b[3]-b[1]) * (a[2]*a[1] - a[0]*a[3])) / determinant_principal);
}

Point2f intersection_droites(pair<float,float> d1, pair<float,float> d2 )
{
	if ( d1.first < 0 and d1.second < 0 and d2.first < 0 and d2.second < 0 )	return Point2f(-1,-1);
	if ( d1.first < 0 and d1.second < 0 )										return Point2f(-d1.second, d2.second - d1.second * d1.first);
	if ( d2.first < 0 and d2.second < 0 )										return Point2f(-d2.second, d1.second - d2.second * d2.first);
	if ( ABS( d1.first - d2.second ) < 0.001 )									return Point2f(-1,-1);

	return Point2f ( (d1.second - d2.second) / (d2.first - d1.first) , (d1.second * d2.first - d2.second * d1.first ) / (d2.first- d1.first) );
}

Point2f intersection_segment(Point2f a1, Point2f a2, Point2f b1, Point2f b2)
{
	float determinant_principal =  (a1.y-a2.y)*(b2.x-b1.x) - (a1.x-a2.x) * (b2.y-b1.y);
	if ( ABS(determinant_principal) < 0.001 )
		return Point2f(-1, -1);
	return Point2f( 	( (b2.x-b1.x) * (a2.x*a1.y - a1.x*a2.y) + (a1.x-a2.x) * (b2.x*b1.y - b1.x*b2.y)) / determinant_principal,
						( (a1.y-a2.y) * (b2.x*b1.y - b1.x*b2.y) + (b2.y-b1.y) * (a2.x*a1.y - a1.x*a2.y)) / determinant_principal);
}

/*
 * Enregistre dans l'instance de Plage_cartes une carte détectée, représentée par la liste de ses quatre sommets.
 * La fonction vérifie si la carte détectée ne se superpose pas avec une carte précédamment détectée.
 */

bool Plage_cartes::ajouter(Point2f* carte)
{
	float aire_nouvelle = AIRE(carte[0], carte[1], carte[2], carte[3]);
	Point2f milieu;

	for (int i = 0; i < plages.size(); i+=4)
	{
		if ( aire_nouvelle < AIRE(plages[i], plages[i+1], plages[i+2], plages[i+3]))
		{
			milieu = intersection_segment(carte[0], carte[2], carte[1], carte[3]);
			if (DETERMINANT(plages[i  ], milieu, milieu, plages[i+1]) > 0 xor DETERMINANT(plages[i+2], milieu, milieu, plages[i+3]) > 0) continue;
			if (DETERMINANT(plages[i+1], milieu, milieu, plages[i+2]) > 0 xor DETERMINANT(plages[i+3], milieu, milieu, plages[i  ]) > 0) continue;
			return false;
		}
		else
		{
			milieu = intersection_segment(plages[i], plages[i+2], plages[i+1], plages[i+3]);
			if (DETERMINANT(carte[0], milieu, milieu, carte[1]) > 0 xor DETERMINANT(carte[2], milieu, milieu, carte[3]) > 0) continue;
			if (DETERMINANT(carte[1], milieu, milieu, carte[2]) > 0 xor DETERMINANT(carte[3], milieu, milieu, carte[0]) > 0) continue;
			return false;
		}
	}

	plages.push_back(carte[0]);
	plages.push_back(carte[1]);
	plages.push_back(carte[2]);
	plages.push_back(carte[3]);

	return true;
}

extern Mat image;
extern ofstream fichier;

void Plage_cartes::toutes(vector<Mat>& les_cartes) const
{
	les_cartes.clear();
	for (int i = 0; i < plages.size(); i+=4)
		les_cartes.push_back(extraire_carte(i));
}

Mat Plage_cartes::la_plus_grande()
{
	if (plages.empty()) return Mat();
	int indice_max;
	float aire_max = 0; float aire_temp;
	for (int i = 0; i < plages.size(); i+=4)
	{
		if ( (aire_temp = AIRE(plages[i], plages[i+1], plages[i+2], plages[i+3])) > aire_max )
		{
			aire_max = aire_temp;
			indice_max = i;
		}
	}
	Mat toto = extraire_carte(indice_max);
	plages.erase(plages.begin()+indice_max, plages.begin()+indice_max+4);
	return toto;
}

void Plage_cartes::multiplier_plage(float facteur)
{
	for (int i = 0; i < plages.size(); i++)
	{
		plages[i] = Point2f(plages[i].x * facteur, plages[i].y * facteur);
	}
}

// découpe une carte d'une image, étant donnée ses quatre sommets.

Mat Plage_cartes::extraire_carte(int indice) const
{
	Mat homo;
	if (indice >= plages.size()) return homo;
	vector<Point2f> rectangle2f(plages.data() + indice, plages.data() + indice + 4);

	bool sens_rotation = DETERMINANT(rectangle2f[0], rectangle2f[1], rectangle2f[1], rectangle2f[2]) > 0;

	float min_choux = MIN(DISTANCE(rectangle2f[0], rectangle2f[1]), DISTANCE(rectangle2f[2], rectangle2f[3]));
	float min_fleur = MIN(DISTANCE(rectangle2f[2], rectangle2f[1]), DISTANCE(rectangle2f[0], rectangle2f[3]));

	if ( min_choux * 1.3 < min_fleur or ( min_fleur * 1.3 > min_choux and
				MAX(MIN(rectangle2f[0].y, rectangle2f[1].y), MIN(rectangle2f[2].y, rectangle2f[3].y)) >
				MAX(MIN(rectangle2f[2].y, rectangle2f[1].y), MIN(rectangle2f[0].y, rectangle2f[3].y))) )
	{
		if (sens_rotation)
			homo = getPerspectiveTransform(rectangle2f, dimensions_choux);
		else
			homo = getPerspectiveTransform(rectangle2f, dimensions_xuohc);
	}
	else
	{
		if (sens_rotation)
			homo = getPerspectiveTransform(rectangle2f, dimensions_fleur);
		else
			homo = getPerspectiveTransform(rectangle2f, dimensions_ruelf);
	}

	Mat carte_bis(300,550, CV_8UC3);
	warpPerspective(image, carte_bis, homo, Size(300, 550));
	return carte_bis;
}

extern Plage_cartes homographie;
extern float sous_echantillon_isolation;
extern float sous_echantillon_reconnaissance;
extern string chemin_carte;
extern bool mode_demi_image;

// affichage interactif des cartes détectées.

void Plage_cartes::sauver_images() const
{
	int compteur = 0, compteur_liste = -1;
	Mat une_carte;

	image = imread(chemin_carte,1);
	if (mode_demi_image) image = image(Range(image.rows*3/5, image.rows),Range::all());
	resize(image, image, Size(), sous_echantillon_reconnaissance, sous_echantillon_reconnaissance);
	homographie.multiplier_plage(sous_echantillon_reconnaissance / sous_echantillon_isolation);

	while ( not (une_carte = homographie.la_plus_grande()).empty())
	{
		compteur++;
		imshow("drawing", une_carte);
		stringstream ss;
		if ( waitKey(0) == 115 )
		{
				ss << "/tmp/image " << compteur << ".png";
				imwrite(ss.str(), une_carte);
		}
	}
}

float somme_cube(Mat& histo, int i)
{
	float somme = 0;
	for ( char a = 0 ; a < 4 ; a ++ )
	{
		for ( char b = 0 ; b < 4 ; b ++ )
		{
			for ( char c = 0 ; c < 4 ; c ++ )
			{
				somme += histo.at<float>((i/25)*4+a,((i/5)%5)*4+b,(i%5)*4+c);
			}
		}
	}
	return somme;
}
