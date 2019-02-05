#include "Isolation.hpp"

bool tri_descendant(Vec4i l1, Vec4i l2) { return l1[1] > l2[1]; }

Dijkstra::Dijkstra(vector<Vec4f>& lignes_) : taille(lignes_.size()), marque(taille), pater(taille), origine(taille), angle(taille), distance(taille)
{
	teste.assign(taille, false);
	lignes.assign(lignes_.begin(), lignes_.end());
	sort(lignes.begin(), lignes.end(), tri_descendant);
}

extern float plafond_note_distance;
extern float intolerance_angle;
extern Plage_cartes homographie;
extern Point2f* points;
extern Mat image;

extern string chemin_absolu; // TODO: To remove

float Dijkstra::note(int i, int j, bool& origin, bool& orth, bool raccordement) const					// distance entre deux segments
{
	float longueur_i = sqrt(DISTANCE(Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[i][2], lignes[i][3])));

	orth =	ABS(SCALAIRE( Point2f(lignes[i][2], lignes[i][3]), Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[j][0], lignes[j][1]))) < 0.98 * longueur_i *
			sqrt(DISTANCE(Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[j][0], lignes[j][1]))) or
			ABS(SCALAIRE( Point2f(lignes[i][2], lignes[i][3]), Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[j][2], lignes[j][3]))) < 0.98 * longueur_i *
			sqrt(DISTANCE(Point2f(lignes[i][0], lignes[i][1]), Point2f(lignes[j][2], lignes[j][3]))) ;

	float d_origin = DISTANCE(Point2f(lignes[i][origine[i]? 2:0], lignes[i][origine[i]? 3:1]), Point2f(lignes[j][0], lignes[j][1]));
	float d_extrem = DISTANCE(Point2f(lignes[i][origine[i]? 2:0], lignes[i][origine[i]? 3:1]), Point2f(lignes[j][2], lignes[j][3]));

	if (raccordement) return d_origin;
	origin = d_origin < d_extrem;

	return MIN(d_origin, d_extrem);
}

bool Dijkstra::parcours_graphe(int sommet)								// algo de Dijkstra modifié
{
	init = sommet;
	teste[init] = true;
	for (int i = 0; i < taille; i++)
	{
		marque[i] = false;
		pater[i] = -1;
		distance[i] = -2;
		angle[i] = 0;
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
			if ( j == pater[sommet] or j == sommet )					continue;
			note_tmp = note(sommet, j, orig, orth );
			if ( note_tmp + distance[sommet] > plafond_note_distance )	continue;
			if ( orth and angle[sommet] > 3)							continue;
			if ( distance[j] > note_tmp + distance[sommet] or distance[j] < -1 )
			{
				if (orth)	angle[j] = angle[sommet] + 1;
				else 		angle[j] = angle[sommet];
				origine[j]	= orig;
				distance[j] = note_tmp + distance[sommet];
				pater[j]	= sommet;
			}
		}
		marque[sommet] = true;

		sommet = -1;
		for (int j = 0; j < taille; j++) if ( not marque[j] and distance[j] > -1 )
		{
			if ( sommet == -1 or distance[sommet] > distance[j] ) sommet = j;
		}
		if ( sommet == -1 )
		{
			pater[init] = 0;
			break;
		}
	}
	float note_min = plafond_note_distance;
	int i_min = -1;
	for (int i = 0; i < taille; i++)
	{
		if ( i == init ) continue;

		if ( distance[i] > -1 and (note_tmp = note(i, init, orig, orth, true) + distance[i]) < note_min )
		{
			if ( angle[i] + (orth?1:0) == 4)
			{
				note_min = note_tmp;
				i_min = i;
			}
		}
	}
	if ( i_min != -1 )
	{
		pater[init] = i_min;
		reconstituer_chemin();
		pater[init] = -1;
	}
	return true;
}

int Dijkstra::non_teste() const
{
	static int compt = 0;
	while (teste[compt])
	{
		compt ++;
		if ( compt == lignes.size() ) return -1;
	}
	return compt;
}

#define REMPLIR_TOTO(indice) while ( angle[sommt] == indice) { toto.push_back(lignes[sommt]); sommt = pater[sommt]; teste[sommt] = true; }
#define ALERTE_COTE_VIDE if ( toto.size() == 0 ) { ofstream log (chemin_absolu + "log.txt");log << "ERREUR : l'un des côtés de la carte est vide" << endl; log.close(); return; } // TODO: To remove


void Dijkstra::reconstituer_chemin()									// vérifie la vraisemblance d'un contour.
{
	int sommt = pater[init];
	vector<Vec4i> toto;
	pair<float,float> droites[4];

	ofstream log (chemin_absolu + "log.txt"); // TODO: to remove

	/*
	 * Les quatres côtés de la cartes sont estimées par des droites, obtenues par régression linéaire
	 * à partir des différents segments qui les constituent.
	 */

	REMPLIR_TOTO(4)
	vector<Vec4i> toto_init(toto.begin(), toto.end());
	toto.clear();

	REMPLIR_TOTO(3)
	ALERTE_COTE_VIDE
	droites[3] = regression(toto);
	toto.clear();

	REMPLIR_TOTO(2)
	ALERTE_COTE_VIDE
	droites[2] = regression(toto);
	toto.clear();

	REMPLIR_TOTO(1)
	ALERTE_COTE_VIDE
	droites[1] = regression(toto);
	toto.clear();

	REMPLIR_TOTO(0)
	toto.insert(toto.end(), toto_init.begin(), toto_init.end());
	ALERTE_COTE_VIDE
	droites[0] = regression(toto);

	// Les quatre sommets de la cartes sont calculés en prenant l'intersection des droites représentant les quatre côtés.

	points[0] = intersection_droites(droites[1], droites[0]);
	points[1] = intersection_droites(droites[2], droites[1]);
	points[2] = intersection_droites(droites[3], droites[2]);
	points[3] = intersection_droites(droites[0], droites[3]);

	#ifdef DEBUG_
	log << "aire : " << AIRE(points[0],points[1],points[2],points[3])	<< " - " << points[0].x << "," << points[0].y
																		<< " - " << points[1].x << "," << points[1].y
																		<< " - " << points[2].x << "," << points[2].y
																		<< " - " << points[3].x << "," << points[3].y << "    ";// TODO: To remove

#endif

	/*
	 * vérification de la vraisemblance de ce rectangle, en effectuant les testes suivants :
	 * - les sommets sortent-ils de l'image ?
	 * - l'aire est-elle suffisamment grande ?
	 * - Y-a-t-il des côtés de longueur très faible ?
	 * - Le quadrilatère trouvé est-il trop loin d'une rectangle ?
	 * - La carte trouvée se superpose-t-elle avec une carte déjà détectée ?
	 *
	 */

	if ( 	points[0].x < -50 or points[0].y < -50 or points[1].x < -50 or points[1].y < -50 or points[2].x < -50 or points[2].y < -50 or points[3].x < -50 or points[3].y < -50 or
			points[0].x > image.cols + 50 or points[0].y > image.rows + 50 or
			points[1].x > image.cols + 50 or points[1].y > image.rows + 50 or
			points[2].x > image.cols + 50 or points[2].y > image.rows + 50 or
			points[3].x > image.cols + 50 or points[3].y > image.rows + 50)
	{
		#ifdef DEBUG_
		log << "point invalide" << endl;// TODO: To remove
        #endif
		log.close();// TODO: To remove
		return;
	}

	if (AIRE(points[0],points[1],points[2],points[3]) * 50 < image.rows * image.cols)
	{
		#ifdef DEBUG_
		log << "trop petite" << endl;// TODO: To remove
        #endif
		log.close();// TODO: To remove
		return;
	}

	double a, b, c, d;
	if ((a =	DISTANCE(points[0], points[1]))	< 30 or
		(b =	DISTANCE(points[2], points[1]))	< 30 or
		(d =	DISTANCE(points[0], points[3]))	< 30 or
		(c =	DISTANCE(points[3], points[2]))	< 30 )
	{
		#ifdef DEBUG_
		log << "côté trop petit" << endl;// TODO: To remove
        #endif
		log.close();// TODO: To remove
		return;
	}

	if (MIN4(a,b,c,d) * 70 < MAX4(a,b,c,d))
	{
		#ifdef DEBUG_
		log << "proportions trop extrèmes" << endl;// TODO: To remove
        #endif
		log.close();// TODO: To remove
		return;
	}

	#ifdef DEBUG_
	if (not homographie.ajouter(points)) log << "carte refusée par la classe Plage_carte" << endl;// TODO: To remove
	else log << "OK" << endl;// TODO: To remove
	#endif

	#ifndef DEBUG_
	homographie.ajouter(points)
    #endif
	log.close();
}

extern int taille_minimale_segment;
extern float correction_saturation;

void isolation_dijkstra(string nom)
{
	vector<Vec4f> lignes = isolation(nom);

	int i;

	Dijkstra instance(lignes);
	while ((i = instance.non_teste()) != -1)
		instance.parcours_graphe(i);

	vector<Mat> carte_rt;
	homographie.toutes(carte_rt);
	#ifdef DEDFGBUG_
	if (carte_rt.size() > 0) imshow("img0", carte_rt[0]);
	if (carte_rt.size() > 1) imshow("img1", carte_rt[1]);
	if (carte_rt.size() > 2) imshow("img2", carte_rt[2]);
	if (carte_rt.size() > 3) imshow("img3", carte_rt[3]);
	if (carte_rt.size() > 4) imshow("img4", carte_rt[4]);
	waitKey(0);
	#endif
}
