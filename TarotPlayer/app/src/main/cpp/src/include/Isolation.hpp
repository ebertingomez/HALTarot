#ifndef _ISOLATION_H
#define _ISOLATION_H

#include "Cache.hpp"
#include "annexe.hpp"
#include "Histogramme.hpp"
#include <queue>
#include <list>


using namespace std;
using namespace cv;

bool isolation_dijkstra		(string nom, bool table);
bool isolation_histogramme	(Mat& image, int seuil, bool table = true, char canal = 2);


bool sauver_image(Mat const& carte, Point2f* rectangle, bool trouve_orientation = true);

class Dijkstra
{

private:
	int init, taille, autre_branche;
	vector<Vec4f> lignes;
	vector<Vec4f> projec;
	vector<bool> angle;
	vector<bool> marque;
	vector<int> pater;
	vector<float> distance;
	vector<bool> origine;
	vector<bool> teste;

	float note(int i, int j, bool& origin, bool& orth) const;
	int restituer_angles(int sommet) const;

public:
	Vec4f& getLigne(int i) { return lignes[i]; }

	Dijkstra(vector<Vec4f>& lignes);
	bool parcours_graphe(int sommet);
	int non_teste() const;
	void reconstituer_chemin(vector<int>* liste) const;
};

bool verification_rectangle(Dijkstra* instance);

class CartesProbable
{

private:

	vector<string> carte_passe;
	vector<string> jeu;

public:

	int restant(Classification type);
	void carte_vue(string laquelle) { carte_passe.push_back(laquelle); }

};

#endif
