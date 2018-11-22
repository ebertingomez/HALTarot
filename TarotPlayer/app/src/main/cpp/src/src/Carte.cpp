#include "Carte.hpp"
#include "Isolation.hpp"

//#define DEBUG_HOMOGRAPHIE

using namespace std;
using namespace cv;

void shell()
{
	char commande[50];
	char* fichier;
	Carte paquet;

	while ( strcmp(commande, "q") && strcmp(commande, "quit") && strcmp(commande, "stop") && strcmp(commande, "exit") )
	{
		printf(">  ");
		scanf("%s", commande);

		if ( strcmp(commande, "cherche") == 0 )
		{
			scanf("%s", commande);
			cout << "------------>  " << paquet.analyse(commande) << endl;
		}
		else if ( strcmp(commande, "découpe_table") == 0 )
		{
			scanf("%s", commande);
			Mat image = imread( string(commande), 1);
			image = image(Range(image.rows/2, image.rows),Range::all());

			for (int seuil = 150; seuil < 230; seuil += 5)
				if (isolation_histogramme(image, seuil))
					break;


			//isolation_dijkstra(fichier, true);
		}
		else if ( strcmp(commande, "découpe") == 0 )
		{
			scanf("%s", commande);
			isolation_dijkstra(string(commande), false);
		}
	}
}

Carte::Carte(string chemin):paquet(), cache(true)
{
    Histogramme::cache = &cache;
    if ( cache.verification_acces())
    {
		if ( chemin[chemin.length()-1] != '/' ) chemin += '/';
		DIR* dossier = opendir(chemin.c_str());
		vector<char*> carte;
		if ( dossier)
		{
			char* nom;
			struct dirent* fichier = NULL;
			while ( (fichier = readdir(dossier)) != NULL )
			{
				nom = fichier->d_name;
				if (classification(nom, true) == AUTRE) paquet.push_back(new Algorithme_surf(chemin + string(nom)));
			}
			closedir(dossier);
		} else cout << "ERREUR : dossier image innaccessible" << endl;

		for ( int i = 0 ; i < paquet.size() ; i ++ )
			paquet[i]->calcul_descripteurs();
	}
}

Carte::Carte():paquet(), cache(false)
{
	cache.charge_couleurs();
	Histogramme::cache = &cache;

	int occurences;
	char code[3];

	while ( (occurences = cache.lire_carte(code)) != -1 )
	{
		paquet.push_back(new Algorithme_surf(string(code), occurences));
	}
	cache.attribuer_teinte(paquet);
}

Carte::~Carte()
{
	for (int i = 0; i < paquet.size(); i++)
	{
		delete paquet[i];
	}

}

string chemin_absolu;

string Carte::analyse(string const& nom_fichier)
{
	chemin_absolu = nom_fichier + "/";
	Algorithme_surf image(chemin_absolu + "image.png");

	if ( image.getCode() == "!!" )
	{
		cout << "ERREUR : " << chemin_absolu << "image.png" << " inaccessible" << endl;
		return "!!";
	}

	image.histogramme();

	int hauteur_carte = 0;
	Classification couleur = image.classification_carte(&hauteur_carte);
	switch (couleur)
	{

	case RIEN:
		cout << "carte illisible" << endl;
		return "!!";

	case PETITE_NOIRE:
	case PETITE_ROUGE:
	case PETIT_CARREAU:
	case PETIT_COEUR:
	case PETIT_PIQUE:
	case PETIT_TREFLE:
	{
		stringstream ss;
		ss << hauteur_carte << " de " << transcription_francais(couleur);
		return ss.str();
	}
	case AUTRE:

		cout << "C'est un atout ou un honneur." << endl;

		Mat morceau_;
		if ( image.COLS > image.ROWS )
			morceau_ = image.getImage()(Range(image.ROWS*0.639, image.ROWS),Range(0, image.COLS*0.2));
		else
			morceau_ = image.getImage()(Range(0, image.ROWS*0.2),Range(0, image.COLS*0.361));

		Histogramme morceau(morceau_, image.getCode());
		morceau.histogramme();

		couleur = morceau.classification_carte();

		cout << "C'est un " << transcription_francais(couleur) << endl;

		image.calcul_descripteurs();
		return analyse_SURF(image, couleur);
	}
}

long norme_euclidienne(int* x1, int* x2)
{
	long norme = 0;
	for ( int i = 0 ; i < 20 ; i ++ ) norme += (x1[i]-x2[i])*(x1[i]-x2[i]);
}

string Carte::analyse_SURF(Algorithme_surf banane, Classification couleur)
{
	long tri[78];
	vector<DMatch>* liste_match;
    for ( char i = 0 ; i < paquet.size() ; i ++ )
    {
        tri[i] = -1;
        switch (couleur)
        {
		case HONNEUR_TREFLE:
		case HONNEUR_PIQUE:
		case HONNEUR_COEUR:
		case HONNEUR_CARREAU:
			if ( couleur != classification(paquet[i]->getCode().c_str()) ) continue;
			break;
		case HONNEUR_NOIR:
			if ( paquet[i]->getCode()[1] != 'P' && paquet[i]->getCode()[1] != 'T' ) continue;
			break;
		case HONNEUR_ROUGE:
			if ( paquet[i]->getCode()[1] != 'A' && paquet[i]->getCode()[1] != 'O' ) continue;
			break;
		case ATOUT:
			if ( paquet[i]->getCode()[1] == 'O' || paquet[i]->getCode()[1] == 'A' || paquet[i]->getCode()[1] == 'P' || paquet[i]->getCode()[1] == 'T' ) continue;
		}

		tri[i] = norme_euclidienne(banane.getTeinte(), paquet[i]->getTeinte());
	}

    char minimum;
	while(true)
    {
        minimum = 0;
        for ( char i = 0 ; i  < paquet.size() ; i ++ )
        {
            if ( tri[i] != -1 && (tri[i] < tri[minimum] || tri[minimum] == -1))
                minimum = i;
        }

        if ( tri[minimum] == -1 ) break;

        cout << paquet[minimum]->getCode() << " : " << tri[minimum] << endl;

		liste_match = paquet[minimum]->comparer(banane);
		if ( banane.investigation(*paquet[minimum], *liste_match) )
		{
			delete liste_match;
			return paquet[minimum]->getCode();
		}

        tri[minimum] = -1;
    }
    delete liste_match;
    return "--";
}

void Carte::vers_fichier()
{
	if (!cache.verification_acces()) return;


    short nb_lignes;
    for ( int i = 0 ; i < paquet.size() ; i ++ )
    {
		nb_lignes = paquet[i]->vers_fichier();
		cache.insertion_carte(paquet[i]->getCode(), nb_lignes);
	}
}