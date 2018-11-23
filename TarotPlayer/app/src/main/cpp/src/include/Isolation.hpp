#ifndef _ISOLATION_H
#define _ISOLATION_H

#include "Cache.hpp"
#include "annexe.hpp"
#include "Carte.hpp"
#include "Histogramme.hpp"
#include <queue>
#include <list>


using namespace std;
using namespace cv;

void isolation_rectangle	(string nom);
void isolation_dijkstra		(string nom);

vector<Vec4f> isolation(string nom);



class Dijkstra
{

private:
	int init, taille;
	vector<Vec4f> lignes;
	vector<char> angle;
	vector<bool> marque;
	vector<int> pater;
	vector<float> distance;
	vector<bool> origine;
	vector<bool> teste;

	float note(int i, int j, bool& origin, bool& orth, bool raccordement = false) const;

public:
	Vec4f& getLigne(int i) { return lignes[i]; }

	Dijkstra(vector<Vec4f>& lignes);
	bool parcours_graphe(int sommet);
	int non_teste() const;
	void reconstituer_chemin();
};

#endif
