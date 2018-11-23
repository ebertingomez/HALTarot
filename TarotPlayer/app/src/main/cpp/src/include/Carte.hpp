#ifndef _CARTE_H
#define _CARTE_H

#include "dirent.h"

#include "Algorithme_surf.hpp"
#include "Histogramme.hpp"
#include "annexe.hpp"


class Carte
{
private:

    vector<Algorithme_surf*> paquet;

    set<string> honneur_manquant;
    set<string> petites_manquantes;

    string valeur(float& rapport); 		// met rapport à -1 si la valeur passée en paramètre est trop absurde.

	Cache cache;

public:

    Carte();
    ~Carte();

    string analyse(string const& nom_fichier);
    string analyse(Mat const& nom_fichier);
    string analyse(Algorithme_surf& image);

    string analyse_SURF(Algorithme_surf banane, Classification couleur);

    void vers_fichier();

    Cache getCache() { return cache; }

};

#endif


