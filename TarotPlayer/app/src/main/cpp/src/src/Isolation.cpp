#include "Isolation.hpp"

Point2f points[4];
Mat image;

Point2f intersection_droites(pair<float,float> d1, pair<float,float> d2 )
{
	if ( d1.first < 0 and d1.second < 0 and d2.first < 0 and d2.second < 0 )	return Point2f(-1,-1);
	if ( d1.first < 0 and d1.second < 0 )										return Point2f(-d1.second, d2.second - d1.second * d1.first);
	if ( d2.first < 0 and d2.second < 0 )										return Point2f(-d2.second, d1.second - d2.second * d2.first);
	if ( ABS( d1.first - d2.second ) < 0.001 )									return Point2f(-1,-1);

	return Point2f ( (d1.second - d2.second) / (d2.first - d1.first) , (d1.second * d2.first - d2.second * d1.first ) / (d2.first- d1.first) );
}



Dijkstra::Dijkstra(vector<Vec4f>& lignes_) : taille(lignes_.size()), teste(taille), projec(taille)
{
	for (int i = 0; i < taille ; i++)
	{
		teste[i] = false;
		if ( lignes_[i][2] == lignes_[i][0] )
		{
			projec[i][0] = CV_PI / 2;
			projec[i][1] = lignes_[i][2];
			projec[i][2] = min( lignes_[i][1], lignes_[i][3]);
			projec[i][3] = max( lignes_[i][1], lignes_[i][3]);
		}
		else
		{
			projec[i][0] = atan( (lignes_[i][3] - lignes_[i][1]) / (float)(lignes_[i][2] - lignes_[i][0]) );
			float cos_pente = cos(projec[i][0]) , sin_pente = sin(projec[i][0]);

			projec[i][1] = (float)lignes_[i][1] * cos_pente - (float)lignes_[i][0] * sin_pente;
			projec[i][2] = (float)lignes_[i][0] * cos_pente + (float)lignes_[i][1] * sin_pente;
			projec[i][3] = (float)lignes_[i][2] * cos_pente + (float)lignes_[i][3] * sin_pente;

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

	for (int i = 0; i < projec.size(); i++)
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
					lignes_[j][0] = MIN4(lignes_[i][0],lignes_[i][2],lignes_[j][0],lignes_[j][2]);
					lignes_[j][1] = MIN4(lignes_[i][1],lignes_[i][3],lignes_[j][1],lignes_[j][3]);
					lignes_[j][2] = MAX4(lignes_[i][0],lignes_[i][2],lignes_[j][0],lignes_[j][2]);
					lignes_[j][3] = MAX4(lignes_[i][1],lignes_[i][3],lignes_[j][1],lignes_[j][3]);
				}
				else
				{
					lignes_[j][0] = MIN4(lignes_[i][0],lignes_[i][2],lignes_[j][0],lignes_[j][2]);
					lignes_[j][1] = MAX4(lignes_[i][1],lignes_[i][3],lignes_[j][1],lignes_[j][3]);
					lignes_[j][2] = MAX4(lignes_[i][0],lignes_[i][2],lignes_[j][0],lignes_[j][2]);
					lignes_[j][3] = MIN4(lignes_[i][1],lignes_[i][3],lignes_[j][1],lignes_[j][3]);
				}
				projec.erase(projec.begin()+i);
				lignes_.erase(lignes_.begin()+i);
				i --;
				break;
			}
		}
	}
	marque.resize(taille);
	pater.resize(taille);
	distance.resize(taille);
	origine.resize(taille);
	angle.resize(taille);
	teste.resize(taille);
	lignes.assign(lignes_.begin(), lignes_.end());

}

extern float plafond_note_distance;
extern float intolerance_angle;

float Dijkstra::note(int i, int j, bool& origin, bool& orth) const
{
	if 		( ABS(projec[i][0] - projec[j][0] ) < 0.1 )				orth = false;
	else if ( ABS(projec[i][0] - projec[j][0] ) - CV_PI / 2 < 0.1 )	orth = true;
	else return -1;

	char signe_i(1), signe_j(1);
	if ( not( projec[i][0] >= 0 xor projec[j][0] >= 0 ) )
	{
		if ( ABS(projec[i][0]) < ABS(projec[j][0]) )	signe_i = -1;
		else 											signe_j = -1;
	}

	float depuis;
	if ( origine[i] )	depuis = projec[i][3] * signe_i;
	else 				depuis = projec[i][2] * signe_i;

	if (orth)
	{
		origin = ABS(projec[j][2] * signe_j - projec[i][1] ) < ABS(projec[j][3] * signe_j - projec[i][1] );

		return 		ABS( ABS(projec[i][0] - projec[j][0] ) - CV_PI / 2) * intolerance_angle +
					ABS( (origin ? projec[j][2] : projec[j][3] ) * signe_j - projec[i][1] ) +
					ABS( depuis - projec[j][1]);

	}
	else
	{
		origin = ABS(projec[j][2] * signe_j - depuis) < ABS(projec[j][3] * signe_j - depuis);
		if ( origin xor origine[i] )	return -2;
		return ABS( (origin ? projec[j][2] : projec[j][3] ) * signe_j - depuis ) + ABS( projec[i][1] - projec[j][1] );
	}
}

int Dijkstra::restituer_angles(int sommet) const
{
	int nombre = 0;
	while ( sommet != init )
	{
		if ( sommet == -1 ) return -1;
		if ( angle[sommet] ) nombre ++;
		sommet = pater[sommet];
	}
	return nombre;
}

bool Dijkstra::parcours_graphe(int sommet)
{
	init = sommet;
	teste[init] = true;
	for (int i = 0; i < taille; i++)
	{
		marque[i] = false;
		pater[i] = -1;
		distance[i] = -1;
	}
	origine[sommet] = true;
	distance[sommet] = 0;
	float note_tmp;
	bool orth, orig;
	int mini;

	for (int i = 0; i < taille; i++)
	{
		for (int j = 0; j < taille; j++)
		{
			if ( j == pater[sommet] ) continue;
			note_tmp = note(sommet, j, orig, orth );
			if ( note_tmp < 0 or note_tmp + distance[sommet] > plafond_note_distance ) continue;
			if ( distance[j] > note_tmp + distance[sommet] or distance[j] < 0 )
			{
				angle[j]	= orth;
				origine[j]	= orig;
				distance[j] = note_tmp + distance[sommet];
				pater[j]	= sommet;
				teste[j] = true;
			}
			else if ( distance[j] > -0.5 and distance[sommet] + distance[j] + note_tmp < plafond_note_distance and restituer_angles(j) + restituer_angles(sommet) + (orth?1:0) == 4 )
			{
				autre_branche = j;
				pater[init] = sommet;
				if ( verification_rectangle(this))
					return true;
				pater[init] = -1;
			}

		}
		marque[sommet] = true;

		sommet = -1;
		for (int j = 0; j < taille; j++) if ( not marque[j] and distance[j] > 0 )
		{
			if ( sommet == -1 or distance[sommet] > distance[j] ) sommet = j;
		}
		if ( sommet == -1 )
		{
			pater[init] = 0;
			return 0;
		}
	}
	return true;
}

int Dijkstra::non_teste() const
{
	static int compt = -1;
	compt ++;
	if ( compt == lignes.size() ) return -1;
	else return compt;

	for (int i = 0; i < teste.size(); i++)
	{
		if ( not teste[i] ) return i;
	}
	return -1;
}

void Dijkstra::reconstituer_chemin(vector<int>* liste) const
{
	int sommt = pater[init];
	while ( sommt != init )
	{
		liste[(restituer_angles(sommt))%4].push_back(sommt);
		sommt = pater[sommt];
	}
	sommt = autre_branche;
	while ( sommt != init )
	{
		liste[(4-restituer_angles(sommt))%4].push_back(sommt);
		sommt = pater[sommt];
	}
	liste[0].push_back(init);
}


bool verification_rectangle(Dijkstra* instance)
{
	vector<int> composantes[4];
	pair<float,float> droites[4];
	instance->reconstituer_chemin(composantes);
	for (int i = 0; i < 4; i++)
	{
		vector<Vec4f> toto;
		for (int j = 0; j < composantes[i].size(); j++)
			toto.push_back(instance->getLigne(composantes[i][j]));

		droites[i] = regression(toto);
	}
	for (int i = 0; i < 4; i++)
		points[i] = intersection_droites(droites[i], droites[(i+1)%4]);

	float rapport = DISTANCE(points[0], points[1]) / DISTANCE(points[1], points[2]);
	if ( rapport < 1 ) rapport = 1 / rapport;

	return vraisemblance_rectangle(points, image.rows, image.cols) and ABS( rapport - 3.35 ) < 0.55 ;
}

extern int taille_minimale_segment;

bool isolation_dijkstra(string nom, bool table)
{
	Plage_cartes homographie;
	Mat color_dst;
	image = imread(nom, 1);

	if (table)	image = image(Range(image.rows/2, image.rows),Range::all());

	vector<Vec4f> lignes;

	Mat gris;
	extractChannel(image, gris, 2);
	Ptr<LineSegmentDetector> algo = createLineSegmentDetector();
	algo->detect(gris, lignes);
	for (vector<Vec4f>::iterator i = lignes.begin() ; i != lignes.end() ; i++)
		if ( (((*i)[0] - (*i)[2])*((*i)[0] - (*i)[2]) + ((*i)[1] - (*i)[3])*((*i)[1] - (*i)[3])) < taille_minimale_segment )
		{
			i--;
			lignes.erase(i+1);
		}


	Size taille_image = image.size();
	if (table)
	{
		homographie.transformer(lignes);
		vector<Vec4f> taille_image_bis(2);
		taille_image_bis[0] = {0, 0, (float)image.rows, 0};
		taille_image_bis[1] = {(float)image.rows, (float)image.cols, 0, (float)image.cols};
	}

	#ifdef DEBUG_
	for( int i = 0; i < lignes.size(); i++ )
		line( image, Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[i][2], lignes[i][3]), Scalar(0,0,255), 2, 8);
	#endif
	Dijkstra instance(lignes);

	int sommt;
	Point2f points_max[4];
	int aire_max = 0, aire;

	while ( (sommt = instance.non_teste()) != -1 )
		if ( instance.parcours_graphe(sommt) )
		{
			if (table) homographie.transformer_inverse();
			if ( (aire = AIRE(points[0], points[1], points[2], points[3])) > image.rows * image.cols / 20 )
				for( int i = 0; i < 4; i++ )
					line( image, points[i], points[(i+1)%4], Scalar(255,0,0), 2, 8 );
			if (aire > aire_max)
			{
				aire_max = aire;
				points_max[0] = points[0];
				points_max[1] = points[1];
				points_max[2] = points[2];
				points_max[3] = points[3];
			}
		}

	imshow("kg",image);
	waitKey(0);
	return true;
 	/*if ( not sauver_image(imread(nom,1), points_max))
	{
		cout << "aucune carte détectée !" << endl;
		imshow( "Detected Lines", image );
		waitKey(0);
		return false;
	}*/
	return true;
}

extern int epsilon_approx_poly;

bool isolation_histogramme(Mat& image, int seuil, bool table, char canal)
{
	static Plage_cartes toto;

	Histogramme source(image,"--");
	source.histogramme();
	unsigned int* histo;

	if		( canal == 0 )	;//histo = source.getTeinte();
	else if ( canal == 1 )	histo = source.getSaturation();
	else if ( canal == 2 )	histo = source.getValeur();

	Mat dest;
	extractChannel(image, dest, canal);
	compare(dest, Scalar(seuil), dest, CMP_LE );

	vector<vector<Point>> contours;
	vector<Vec4i> hierarchy;
	vector<Point> rectangle;
	vector<Point2f> rectangle2f;

	int noeud; bool bravo = false;
	queue<int> 	attente;
	Scalar color = Scalar(255, 255, 255);

	#ifdef DEBUG_
	imshow("gert",dest);
	waitKey(0);
	#endif

	findContours(dest,contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
	for ( noeud = 0 ; noeud < contours.size() ; noeud ++ ) if ( hierarchy[noeud][3] < 0 ) break;
	if ( contours[noeud].size() == 4 ) attente.push(hierarchy[noeud][2]);

	while ( true )
	{

		if ( contours[noeud].size() > 15)
		{
			approxPolyDP(contours[noeud], rectangle, sqrt(contourArea(contours[noeud]))/epsilon_approx_poly, true);

			if ( rectangle.size() == 4 && AIRE(rectangle[0], rectangle[1], rectangle[2], rectangle[3]) > image.rows * image.cols / 40 )
			{
				Mat(rectangle).convertTo(rectangle2f, CV_32F);
				if (toto.ajouter(rectangle2f)) cout << "oui" << endl;
					//sauver_image(image, rectangle2f.data(), false);
			}
			else attente.push(hierarchy[noeud][2]);
		}

		noeud = hierarchy[noeud][0];
		while ( noeud < 0 )
		{
			if ( attente.empty() ) return true;
			noeud = attente.front();
			attente.pop();
		}
	}
}

bool sauver_image(Mat const& carte, Point2f* rectangle, bool trouve_orientation)
{
	static int compteur = 0;
	Mat homo, carte_bis;
	vector<Point2f> rectangle2f(rectangle, rectangle+4);

	if ( trouve_orientation )
	{
		if ( 	sqrt(DISTANCE(rectangle[0], rectangle[1])) + sqrt(DISTANCE(rectangle[2], rectangle[3])) <
				sqrt(DISTANCE(rectangle[2], rectangle[1])) + sqrt(DISTANCE(rectangle[0], rectangle[3])) )
			homo = getPerspectiveTransform(rectangle2f, dimensions_choux);
		else
			homo = getPerspectiveTransform(rectangle2f, dimensions_fleur);
	}
	else
	{
		if ( 	MAX(MIN(rectangle[0].y, rectangle[1].y), MIN(rectangle[2].y, rectangle[3].y)) >
				MAX(MIN(rectangle[2].y, rectangle[1].y), MIN(rectangle[0].y, rectangle[3].y)) )
			homo = getPerspectiveTransform(rectangle2f, dimensions_choux );
		else
			homo = getPerspectiveTransform(rectangle2f, dimensions_fleur );
	}


	warpPerspective(carte, carte_bis, homo, Size(300, 550));

	imshow("drawing", carte_bis);
	stringstream ss;
	switch ( waitKey(0) )
	{
	case 115:
	{
		ss << "/tmp/image " << compteur << ".png";
		compteur ++;
		return false;
	}
	case 10:
	{
		cout << rectangle[0].x << " " << rectangle[0].y << endl <<
				rectangle[1].x << " " << rectangle[1].y << endl <<
				rectangle[2].x << " " << rectangle[2].y << endl <<
				rectangle[3].x << " " << rectangle[3].y << endl;
		return true;
	}
	default:
		return false;
	}
}

int CartesProbable::restant(Classification type)
{
	int nombre = 0;

	for ( int i = 0 ; i < carte_passe.size() ; i ++ )
	{
		if ( classification(carte_passe[i]) == type )
			nombre ++;
		else if ( classification(carte_passe[i], true) == type )
			nombre ++;
		else if ( type == HONNEUR_ROUGE && (classification(carte_passe[i]) == HONNEUR_CARREAU || classification(carte_passe[i]) == HONNEUR_COEUR ))
			nombre ++;
		else if ( type == HONNEUR_NOIR && (classification(carte_passe[i]) == HONNEUR_PIQUE || classification(carte_passe[i]) == HONNEUR_TREFLE ))
			nombre ++;
	}
	return nombre;
}
