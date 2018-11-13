#ifndef _HISTOGRAMME_H
#define _HISTOGRAMME_H

#include "Cache.hpp"
#include "annexe.hpp"
#include <algorithm>

using namespace std;
using namespace cv;

class Histogramme
{
protected:

	Mat image;

private:

    string code;

    Mat histo;             // histogramme HSV
    int note_total, rapport_rouge;

    unsigned int valeur[20] = {0}; // histogramme de la valeur des couleurs peu saturées
	unsigned int saturation[20] = {0};
	unsigned int valeur_glob[20] = {0};
			 int teinte[20] = {0};

	Classification classification_couleur(Mat const& image, bool rouge, bool honneur, int* nombre_match);


public :

	Histogramme(string const& fichier);
	Histogramme(Mat image, string code);
	Histogramme(string code, bool ne_sert_a_rien);

    void histogramme();
    void teinte_atout();
    float distance_teinte(Histogramme const& autre);
    Classification classification_neuronale(bool honneur);
    Classification classification_carte(int* hauteur_carte = 0);

    void affiche_image(string const& titre) { imshow(titre,image); waitKey(0); }

    static bool maxima(unsigned int* valeur, int& max, int& second);

    string getCode()				{ return code; }
    Mat& getHisto()					{ return histo; }
    Mat& getImage()					{ return image; }
    unsigned int* getValeur()		{ return valeur_glob; }
    unsigned int* getSaturation()	{ return saturation; }
    int getTeinte(int i) const		{ return teinte[i]; }
    int* getTeinte()				{ return teinte; }
    void setTeinte(int* toto) { for ( int i = 0 ; i < 20 ; i ++ ) teinte[i] = toto[i]; }

    double comparer(Histogramme autre) { return compareHist( histo, autre.getHisto(), 2 ); }

    static Cache* cache;

};

#endif
