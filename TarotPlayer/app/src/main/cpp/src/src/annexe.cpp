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

pair<float, float> regression(vector<Vec4f> lignes)
{
	float moyenne_x = 0;
	float moyenne_y = 0;

	for (int i = 0; i < lignes.size(); i++)
	{
		moyenne_x += lignes[i][0] + lignes[i][2];
		moyenne_y += lignes[i][1] + lignes[i][3];
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

extern Point2f points[];

void Plage_cartes::transformer(vector<Vec4f>& lignes)
{
	Mat un_point(2,2,CV_32S);
	for (int i = 0; i < lignes.size(); i++)
	{
		un_point.ptr(0)[0] = lignes[i][0];
		un_point.ptr(0)[1] = lignes[i][1];
		un_point.ptr(1)[0] = lignes[i][2];
		un_point.ptr(1)[1] = lignes[i][3];

		perspectiveTransform(un_point, un_point, H_en_vigueur);

		lignes[i][0] = un_point.ptr(0)[0];
		lignes[i][1] = un_point.ptr(0)[1];
		lignes[i][2] = un_point.ptr(1)[0];
		lignes[i][3] = un_point.ptr(1)[1];
	}
}

void Plage_cartes::transformer_inverse()
{
	vector<Point2f> points_(points, points+4);
	perspectiveTransform(points_, points_, H_en_vigueur.inv());
	points[0] = points_[0];
	points[1] = points_[1];
	points[2] = points_[2];
	points[3] = points_[3];
}
void Plage_cartes::appliquer(){}

Point2f intersection_segment(Point2f a1, Point2f a2, Point2f b1, Point2f b2)
{
	float determinant_principal =  (a1.y-a2.y)*(b2.x-b1.x) - (a1.x-a2.x) * (b2.y-b1.y);
	if ( determinant_principal < 0.001 )
		return Point2f(-1, -1);
	return Point2f( 	( (b2.x-b1.x) * (a2.x*a1.y - a1.x*a2.y) + (a1.x-a2.x) * (b2.x*b1.y - b1.x*b2.y)) / determinant_principal,
						( (a1.y-a2.y) * (b2.x*b1.y - b1.x*b2.y) + (b2.y-b1.y) * (a2.x*a1.y - a1.x*a2.y)) / determinant_principal);
}

Plage_cartes::Plage_cartes(): H_en_vigueur(3,3,CV_64F) {}

bool Plage_cartes::ajouter(vector<Point2f>& carte)
{
	float min_cote = MIN4 (
			DISTANCE(	carte[0], carte[1]	),
			DISTANCE(	carte[0], carte[3]	),
			DISTANCE(	carte[3], carte[2]	),
			DISTANCE(	carte[1], carte[2]	));

	for (int i = 0; i < plages.size(); i++)
	{
		if ( DISTANCE(	intersection_segment(carte[0]		,carte[2]		,carte[1]		,carte[3]) ,
						intersection_segment(plages[i][0]	,plages[i][2]	,plages[i][1]	,plages[i][3]) ) < min_cote ) return false;
	}


	if ( not plages.empty() )
	{
		vector<Point2f> carte_redresse;
		perspectiveTransform(carte, carte_redresse, H_en_vigueur);

		if ( not vraisemblance_rectangle(carte_redresse.data()) ) return false;
		float distance_01 = DISTANCE ( carte_redresse[0], carte_redresse[1]);
		float distance_12 = DISTANCE ( carte_redresse[1], carte_redresse[2]);

		if ( abs( MAX(distance_01, distance_12) / MIN(distance_01, distance_12) - 121.0/36.0 ) > 0.8 ) return false;

		if ( distance_01 > distance_12)
			somme_mat_3x3(H_en_vigueur, getPerspectiveTransform(carte, dimensions_fleur ));
		else
			somme_mat_3x3(H_en_vigueur, getPerspectiveTransform(carte, dimensions_choux ));
	}
	else
	{
		if ( 	MAX(MIN(carte[0].y, carte[1].y), MIN(carte[2].y, carte[3].y)) >
				MAX(MIN(carte[2].y, carte[1].y), MIN(carte[0].y, carte[3].y)) )
			H_en_vigueur = getPerspectiveTransform(carte, dimensions_choux );
		else
			H_en_vigueur = getPerspectiveTransform(carte, dimensions_fleur );
	}

	plages.push_back( new Point2f[4] );
	plages.back()[0] = carte[0];
	plages.back()[1] = carte[1];
	plages.back()[2] = carte[2];
	plages.back()[3] = carte[3];

	return true;
}

Plage_cartes::~Plage_cartes()
{
	for (int i = 0; i < plages.size() ; i++)	delete[] plages[i];
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
