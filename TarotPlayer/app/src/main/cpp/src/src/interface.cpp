#include "Carte.hpp"
#include "Cache.hpp"

void cartes_chien(string path)
{
    Carte paquet;
    ofstream fichier("chien.txt");
	fichier << paquet.analyse(path) << endl;
	fichier.close();
}

void cartes_IA(string path)
{
    Carte paquet;
	ofstream fichier("chien.txt");
	fichier << paquet.analyse(path) << endl;
	fichier.close();
}

void cartes_table(string path, int nombre)
{
    Carte paquet;
	ofstream fichier("chien.txt");
	fichier << paquet.analyse(path) << endl;
	fichier.close();
}
