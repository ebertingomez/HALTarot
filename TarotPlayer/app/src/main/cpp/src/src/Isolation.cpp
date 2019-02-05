#include "Isolation.hpp"

extern Point2f* points;

extern string chemin_absolu; // TODO TO Remove

Mat image;
Plage_cartes homographie;

extern int tolerance_ecart;

bool reconstituer_chemin(vector<Vec4f> const& lignes, vector<Vec4f> const& projec, Vec4i comp)			// vérifie si deux paires de segments forment un rectangle
{

	points[0] = intersection_segment(lignes[comp[3]], lignes[comp[0]]);
	points[1] = intersection_segment(lignes[comp[0]], lignes[comp[1]]);
	points[2] = intersection_segment(lignes[comp[1]], lignes[comp[2]]);
	points[3] = intersection_segment(lignes[comp[2]], lignes[comp[3]]);

	float a = DISTANCE(points[0], points[1]);
	float b = DISTANCE(points[1], points[2]);
	float c = DISTANCE(points[2], points[3]);
	float d = DISTANCE(points[3], points[0]);

	if ((a > b and ABS( (a+c) / (b+d) - 121.0/36) > 0.45) or
		(a < b and ABS( (b+d) / (a+c) - 121.0/36) > 0.45))
		return false;

	float d_, s_;
	int primatre = 0;

	for (int i = 0; i < projec.size() ; i++)
	{
		if ( ABS(projec[i][0] - projec[comp[0]][0]) < 0.2)
		{
			d_ = DISTANCE(				points[0], Point2f(lignes[i][0], lignes[i][1]));
			s_ = SCALAIRE(points[1],	points[0], Point2f(lignes[i][0], lignes[i][1]));
			if (d_ < a and s_ < 20 and d_ - s_*s_/a < tolerance_ecart )
			{
				d_ = DISTANCE(				points[0], Point2f(lignes[i][2], lignes[i][3]));
				s_ = SCALAIRE(points[1],	points[0], Point2f(lignes[i][2], lignes[i][3]));
				if (d_ < a and s_ < 20 and d_ - s_*s_/a < tolerance_ecart)
				{
					primatre += projec[i][3] - projec[i][2];
					continue;
				}
			}
			d_ = DISTANCE(				points[2], Point2f(lignes[i][0], lignes[i][1]));
			s_ = SCALAIRE(points[3],	points[2], Point2f(lignes[i][0], lignes[i][1]));
			if (d_ < c and s_ < 20 and d_ - s_*s_/c < tolerance_ecart )
			{
				d_ = DISTANCE(				points[2], Point2f(lignes[i][2], lignes[i][3]));
				s_ = SCALAIRE(points[3],	points[2], Point2f(lignes[i][2], lignes[i][3]));
				if (d_ < a and s_ < 20 and d_ - s_*s_/c < tolerance_ecart)
				{
					primatre += projec[i][3] - projec[i][2];
					continue;
				}
			}
		}
		else if ( ABS(projec[i][0] - projec[comp[1]][0]) < 0.2)
		{
			d_ = DISTANCE(				points[1], Point2f(lignes[i][0], lignes[i][1]));
			s_ = SCALAIRE(points[2],	points[1], Point2f(lignes[i][0], lignes[i][1]));
			if (d_ < b and s_ < 20 and d_ - s_*s_/b < tolerance_ecart )
			{
				d_ = DISTANCE(				points[1], Point2f(lignes[i][2], lignes[i][3]));
				s_ = SCALAIRE(points[2],	points[1], Point2f(lignes[i][2], lignes[i][3]));
				if (d_ < b and s_ < 20 and d_ - s_*s_/b < tolerance_ecart)
				{
					primatre += projec[i][3] - projec[i][2];
					continue;
				}

			}
			d_ = DISTANCE(				points[3], Point2f(lignes[i][0], lignes[i][1]));
			s_ = SCALAIRE(points[0],	points[3], Point2f(lignes[i][0], lignes[i][1]));
			if (d_ < d and s_ < 20 and d_ - s_*s_/d < tolerance_ecart )
			{
				d_ = DISTANCE(				points[3], Point2f(lignes[i][2], lignes[i][3]));
				s_ = SCALAIRE(points[0],	points[3], Point2f(lignes[i][2], lignes[i][3]));
				if (d_ < d and s_ < 20 and d_ - s_*s_/d < tolerance_ecart)
				{
					primatre += projec[i][3] - projec[i][2];
					continue;
				}

			}
		}
	}

	if ( primatre *5/7 < sqrt(a) + sqrt(b) ) return false;

	return vraisemblance_rectangle(points, image.rows, image.cols);
}

extern float taille_minimale_segment;
extern float correction_saturation;
extern float sous_echantillon_isolation;
extern Carte* paquet_carte;
extern FILE* fichier_synchronisation;
extern bool mode_demi_image;

vector<Vec4f> isolation(string nom)					// recherche les segments dans une image. Cette fonction est commune aux deux algorithmes.
{
	static Ptr<LineSegmentDetector> algo = createLineSegmentDetector();

	ofstream log (chemin_absolu + "log.txt");

	Mat image_hsv;
	image = imread(nom, 1);
	if (mode_demi_image) image = image(Range(image.rows*3/5, image.rows),Range::all());
	resize(image, image, Size(), sous_echantillon_isolation, sous_echantillon_isolation);

	if ( image.empty())
	{
		log << "ERREUR : image innaccessible" << endl;
		log.close();
		exit(-1);
	}

	cvtColor(image, image_hsv, CV_BGR2HSV);
	image_hsv.convertTo(image_hsv, CV_8UC3);

	int cols = image.cols, rows = image.rows;
	if(image_hsv.isContinuous())
	{
		cols *= rows;
		rows = 1;
	}
	for(int i = 0; i < rows; i++)
	{
		uchar* ptr = image_hsv.ptr<uchar>(i);
		for(int j = 0; j < cols; j++)
			ptr[3*j+2] = MAX(0, ptr[3*j+2] - ptr[3*j+1] / correction_saturation);
	}

	vector<Vec4f> lignes;

	Mat gris;
	extractChannel(image_hsv, gris, 2);
	algo->detect(gris, lignes);
	for (vector<Vec4f>::iterator i = lignes.begin() ; i != lignes.end() ; i++)			// suppression des segments trop courts.
	{
		if ( (((*i)[0] - (*i)[2])*((*i)[0] - (*i)[2]) + ((*i)[1] - (*i)[3])*((*i)[1] - (*i)[3])) < image.rows * image.cols * taille_minimale_segment )
		{
			i--;
			lignes.erase(i+1);
		}
	}
	ofstream fichier_test("/tmp/test");
	for( int i = 0; i < lignes.size(); i++ )
	{
		line( image_hsv, Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[i][2], lignes[i][3]), Scalar(0,0,255), 2, 8);
		fichier_test << lignes[i][0] << "," << lignes[i][1] << "," << lignes[i][2] << "," << lignes[i][3] << endl;
	}

	imshow("dthdy",image_hsv);
	waitKey(0);

	fichier_test.close();

	return lignes;
}

void isolation_rectangle(string nom)
{
	vector<Vec4f> lignes = isolation(nom);

	/*
	 * projection des segments sur l'axe parallèle à leur direction :
	 * - projec[i][0] est l'angle entre le segment i et le vecteur (1 0).
	 * - projec[i][1] est la distance entre le point (0 0) et la droite supportant le segment i.
	 * - projec[i][2] et projec[i][3] sont l'abscisse des projections des extrémités du segment i sur la droite parralèle à i passance par (0 0).
	 */

	vector<Vec4f> projec(lignes.size());
	for (int i = 0; i < lignes.size() ; i++)
	{
		if ( lignes[i][2] == lignes[i][0] )
		{
			projec[i][0] = CV_PI / 2;
			projec[i][1] = lignes[i][2];
			projec[i][2] = min( lignes[i][1], lignes[i][3]);
			projec[i][3] = max( lignes[i][1], lignes[i][3]);
		}
		else
		{
			projec[i][0] = atan( (lignes[i][3] - lignes[i][1]) / (float)(lignes[i][2] - lignes[i][0]) );
			float cos_pente = cos(projec[i][0]) , sin_pente = sin(projec[i][0]);

			projec[i][1] = (float)lignes[i][1] * cos_pente - (float)lignes[i][0] * sin_pente;
			projec[i][2] = (float)lignes[i][0] * cos_pente + (float)lignes[i][1] * sin_pente;
			projec[i][3] = (float)lignes[i][2] * cos_pente + (float)lignes[i][3] * sin_pente;

			if (projec[i][0] < 0)
			{
				projec[i][2] *= -1;
				projec[i][3] *= -1;
			}

			if ( projec[i][2] > projec[i][3] )
			{
				float tmp = projec[i][2];
				projec[i][2] = projec[i][3];
				projec[i][3] = tmp;
			}
		}
	}

	for (int i = 0; i < projec.size(); i++)								// fusion des segments alignés
	{
		for (int j = i+1; j < projec.size(); j++)
		{
			if ( ABS( projec[i][0] - projec[j][0] ) < 0.1 and ABS( projec[i][1] - projec[j][1] ) < 10 and projec[i][3] > projec[j][2] and projec[j][3] > projec[i][2] )
			{
				projec[j][0] = ( projec[i][0] + projec[j][0] ) / 2;
				projec[j][1] = ( projec[i][1] + projec[j][1] ) / 2;
				projec[j][2] = min(projec[j][2],projec[i][2]);
				projec[j][3] = max(projec[j][3],projec[i][3]);
				if (projec[i][0] > 0)
				{
					lignes[j][0] = MIN4(lignes[i][0],lignes[i][2],lignes[j][0],lignes[j][2]);
					lignes[j][1] = MIN4(lignes[i][1],lignes[i][3],lignes[j][1],lignes[j][3]);
					lignes[j][2] = MAX4(lignes[i][0],lignes[i][2],lignes[j][0],lignes[j][2]);
					lignes[j][3] = MAX4(lignes[i][1],lignes[i][3],lignes[j][1],lignes[j][3]);
				}
				else
				{
					lignes[j][0] = MIN4(lignes[i][0],lignes[i][2],lignes[j][0],lignes[j][2]);
					lignes[j][1] = MAX4(lignes[i][1],lignes[i][3],lignes[j][1],lignes[j][3]);
					lignes[j][2] = MAX4(lignes[i][0],lignes[i][2],lignes[j][0],lignes[j][2]);
					lignes[j][3] = MIN4(lignes[i][1],lignes[i][3],lignes[j][1],lignes[j][3]);
				}
				projec.erase(projec.begin()+i);
				lignes.erase(lignes.begin()+i);
				i --;
				break;
			}
		}
	}

	vector<pair<int,int>> paralle;
	vector<float> poids;

	for (int i = 0; i < lignes.size(); i++)								// recherche de paire de sements parallèles
	{
		for (int j = i+1; j < lignes.size(); j++)
		{
			if ( ABS ( projec[i][0] - projec[j][0] ) < 0.15 and ABS ( projec[i][1] - projec[j][1] ) > 10 )
			{
				paralle.push_back(pair<int,int>(i,j));
			}
		}
	}

	Mat* carte;

	for (int i = 0; i < paralle.size(); i++)							// recherche de paire de paire de segments formant un rectangle

		for (int j = i+1; j < paralle.size(); j++)

			if ( ABS (ABS( projec[paralle[i].first][0] - projec[paralle[j].first][0] + projec[paralle[i].second][0] - projec[paralle[j].second][0] ) - CV_PI ) < 0.5 )

				if ( reconstituer_chemin(lignes, projec, Vec4i(paralle[i].first, paralle[j].first, paralle[i].second, paralle[j].second)))

					homographie.ajouter(points);

}
