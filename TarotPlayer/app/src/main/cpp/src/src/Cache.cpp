#include "Cache.hpp"
#include "Algorithme_surf.hpp"


extern string chemin_absolu;

string name;



Cache::Cache(bool modif):modif(modif), fichier_couleur(0)
{
	
    name = chemin_absolu+"pointscaracteristiques";
    fichier_carac   = fopen(name.c_str(),modif ? "wb+" : "rb");
    name = chemin_absolu+"descripteurs";
    fichier_desc    = fopen(name.c_str(),modif ? "wb+" : "rb");
    name = chemin_absolu+"liste";
    fichier_liste   = fopen(name.c_str(),modif ? "w+" : "r");

}

bool Cache::verification_acces()
{
	if ( ! fichier_carac )
    {
        cout << "ERREUR : impossible d'ouvrir le fichier contenant les descripteurs" << endl;
        return false;
    }
    if ( ! fichier_desc )
    {
        cout << "ERREUR : impossible d'ouvrir le fichier contenant les points caractéristiques" << endl;
        return false;
    }
	if ( ! fichier_liste )
    {
        cout << "ERREUR : impossible d'ouvrir le fichier contenant la liste des images disponibles" << endl;
        return false;
    }
    return true;
}

Cache::~Cache()
{
	if ( fichier_carac	!= NULL )	fclose(fichier_carac);
	if ( fichier_desc	!= NULL )	fclose(fichier_desc);
	if ( fichier_liste	!= NULL )	fclose(fichier_liste);
	if ( fichier_couleur!= NULL )	fclose(fichier_couleur);
}

void Cache::insertion_points(KeyPoint* points_carac, int occurences)
{
	fwrite(points_carac, sizeof(KeyPoint), occurences, fichier_carac);
}

void Cache::insertion_desc(Mat* desc)
{
	for ( int i = 0 ; i < desc->rows ; i ++ )
		fwrite(desc->ptr<float>(i), sizeof(float), 64, fichier_desc);
}

void Cache::attribuer_teinte(vector<Algorithme_surf*>& paquet)
{

	if ( not fichier_couleur )
	{
        name = chemin_absolu+"couleurs";
        fichier_couleur = fopen(name.c_str(), "r");
	}

	int liste_teinte[22][20];
	char code_toto[3];
	for ( int i = 0 ; i < 22 ; i ++ )
	{
		fscanf(fichier_couleur, "%s %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d", code_toto,
			liste_teinte[i]   , 			liste_teinte[i]+1 , 			liste_teinte[i]+2 , 			liste_teinte[i]+3 , 				liste_teinte[i]+4 ,
			liste_teinte[i]+5 , 			liste_teinte[i]+6 , 			liste_teinte[i]+7 , 			liste_teinte[i]+8 , 				liste_teinte[i]+9 ,
			liste_teinte[i]+10 ,			liste_teinte[i]+11 ,			liste_teinte[i]+12 , 			liste_teinte[i]+13 ,				liste_teinte[i]+14 ,
			liste_teinte[i]+15 ,			liste_teinte[i]+16 ,			liste_teinte[i]+17 , 			liste_teinte[i]+18 ,				liste_teinte[i]+19 );
	}
	for ( int i = 0 ; i < paquet.size() ; i ++ )
	{
		if ( classification(paquet[i]->getCode()) == ATOUT )
		{
			int nombre = 0;
			if (paquet[i]->getCode()[0] == '1') nombre = 10;
			nombre += paquet[i]->getCode()[1] - '0';
			paquet[i]->setTeinte(liste_teinte[nombre]);
		}
		if ( classification(paquet[i]->getCode()) == EXCUSE )
			paquet[i]->setTeinte(liste_teinte[0]);
	}
}

#ifdef CACHE_HISTO
void Cache::insertion_couleur(string code, int* teinte)
{

	if ( not fichier_couleur )
	{
        name = chemin_absolu+"couleurs";
	    fichier_couleur = fopen(name.c_str(), "w");
	}

	fprintf(fichier_couleur, "%s ", code.c_str());
	for ( int i = 0 ; i < 20 ; i ++ ) fprintf(fichier_couleur, "%d ", teinte[i]);
	fprintf(fichier_couleur, "\n");
}
#endif

void Cache::insertion_carte(string nom, int occurences)
{
	fprintf(fichier_liste, "%s %d\n", nom.c_str(), occurences);
}

void Cache::lire_point(KeyPoint* poin) const
{
	fread(poin, sizeof(KeyPoint), 1, fichier_carac);
}

void Cache::lire_desc(float* toto) const
{
	fread(toto, sizeof(float), 64, fichier_desc);
}

void Cache::remplir(vector<Point>& liste, int taille)
{
	int x,y;
	for ( int i = 0 ; i < taille ; i ++ )
	{
		fscanf(fichier_couleur, "%d %d", &x, &y);
		liste.push_back(Point(x,y));
	}
}

bool Cache::charge_couleurs()
{

	pique		= imread(chemin_absolu + "pique.png",	0);
	carreau		= imread(chemin_absolu + "carreau.png",	0);
	coeur		= imread(chemin_absolu + "coeur.png",	0);
	pissenlit	= imread(chemin_absolu + "trefle.png",	0);

	return pique.data && coeur.data && carreau.data && pissenlit.data;
}

int Cache::lire_carte(char* code) const
{
	int nombre;
	if (fscanf(fichier_liste, "%s %d", code, &nombre) == EOF) return -1;
	else return nombre;
}

