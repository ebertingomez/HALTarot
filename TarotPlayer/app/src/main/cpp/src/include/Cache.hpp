#ifndef _CACHE_H
#define _CACHE_H

#include <fstream>
#include <iostream>
#include <string>
#include <cstring>
#include <cstdio>
#include "opencv2/opencv.hpp"
#include "opencv2/dnn.hpp"
#include "opencv2/calib3d.hpp"
#include "opencv2/stitching/detail/matchers.hpp"
#include "opencv2/xfeatures2d/nonfree.hpp"

#define CACHE_HISTO
#define ABS(a) (((a)>0)?(a):-(a))


using namespace std;
using namespace cv;
using namespace cv::ml;

enum Classification {
	RIEN = 0,
	PETITE_NOIRE = 1,
		PETIT_PIQUE = 2,
		PETIT_TREFLE = 3,

	PETITE_ROUGE = 4,
		PETIT_COEUR = 5,
		PETIT_CARREAU = 6,

	AUTRE = 7,
	HONNEUR_NOIR = 8,
		HONNEUR_PIQUE = 9,
		HONNEUR_TREFLE = 10,

	HONNEUR_ROUGE = 11,
		HONNEUR_CARREAU = 12,
		HONNEUR_COEUR = 13,

	ATOUT = 14,
	EXCUSE = 15
};

class Algorithme_surf;

class Cache
{

private:

	FILE* fichier_carac;
	FILE* fichier_desc;
	FILE* fichier_liste;
	FILE* fichier_couleur;

    Mat pique;
    Mat coeur;
    Mat carreau;
    Mat pissenlit;

    short coupe_couleur[2][4];

	bool modif;

	void remplir(vector<Point>& liste, int taille);

public:

	Cache(bool modif);
	~Cache();

	bool verification_acces();

	void insertion_points(KeyPoint* points, int occurences);
	void insertion_carte(string nom, int occurences);
	void insertion_couleur(string code, int* teinte);

	void lire_point(KeyPoint* poin) const;
	void lire_desc(float* toto) const;
	int lire_carte(char* code) const;
	bool charge_couleurs();

    Mat const& getPique()					{ return pique; }
    Mat const& getCoeur()					{ return coeur; }
    Mat const& getCarreau()					{ return carreau; }
    Mat const& getPissenlit()				{ return pissenlit; }

    const Ptr<ANN_MLP> neurones_carte;
    const Ptr<ANN_MLP> neurones_coin;

    void attribuer_teinte(vector<Algorithme_surf*> paquet);

    void insertion_desc(const Mat &desc);
};

#endif
