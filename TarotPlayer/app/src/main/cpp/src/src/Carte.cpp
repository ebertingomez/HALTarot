#include "Carte.hpp"
#include "Isolation.hpp"

using namespace std;
using namespace cv;
extern string chemin_absolu;

Carte* paquet_carte;

Carte::Carte():paquet(), cache(false), petites_manquantes(), honneur_manquant()
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

string Carte::analyse(string const& nom_fichier)
{
	Algorithme_surf image(chemin_absolu + "image.png");

	if ( image.getCode() == "!!" )
	{
		cout << "ERREUR : " << chemin_absolu << "image.png" << " inaccessible" << endl;
		return "!!";
	}

	return analyse(image);
}

string Carte::analyse(Mat const& nom_fichier)
{
	Algorithme_surf image(nom_fichier);
	return analyse(image);
}

string Carte::analyse(Algorithme_surf& image)							// reconnaissance d'une carte
{

	image.histogramme();

	int hauteur_carte = 0;
	stringstream ss;

	/*
	 * on cherche à classifier grossièrement la carte (petit pique, petit coeur ...), ou autre.
	 * "autre" decouvre les atouts et les honneurs,
	 */

	Classification couleur = image.classification_carte(&hauteur_carte);

	hauteur_carte %= 10;

	switch (couleur)
	{

	case RIEN:
		cout << "carte illisible" << endl;
		return "!!";

	case PETITE_NOIRE:
	case PETITE_ROUGE:
		cout << "couleur illisible !" << endl;
		return "!!";
	case PETIT_CARREAU:
		ss << hauteur_carte << "A";
		break;
	case PETIT_COEUR:
		ss << hauteur_carte << "O";
		break;
	case PETIT_PIQUE:
		ss << hauteur_carte << "P";
		break;
	case PETIT_TREFLE:
		ss << hauteur_carte << "T";
		break;
	case AUTRE:

		cout << "C'est un atout ou un honneur." << endl;

		/*
		 * On veut maintenant affiner la classification, pour distinguer les atouts des honneurs.
		 * On découpe maintenant le coin supérieur gauche de la carte. Si c'est un honneur, ce coin contiendra un pique, un carreau, un trèfle ou um coeur.
		 */

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
		string retour = analyse_SURF(image, couleur);						// recherche de matching SURF.

		/*
		 * Les honneurs sont symétriques. Donc la recherche des descripteurs se fait seulement sur la partie supérieure.
		 * En cas d'échec, la section suivante refait la recherche avec la partie inférieure.
		 *
		 */

		if ( retour == "--" and couleur != ATOUT)
		{
			image.calcul_descripteurs(false);
			return analyse_SURF(image, couleur);
		}
		else return retour;
	}

	return ss.str();
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
    for ( char i = 0 ; i < paquet.size() ; i ++ )					// compare l'histogramme en teinte de l'image avec celui des cartes préenregistrées pertinentes.
    {
        tri[i] = -1;
        if ( honneur_manquant.find(paquet[i]->getCode()) != honneur_manquant.end()) continue;
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
        for ( char i = 0 ; i  < paquet.size() ; i ++ )					// la recherche de matching se fait par ordre de distance d'histogramme H décroissante.
        {
            if ( tri[i] != -1 && (tri[i] < tri[minimum] || tri[minimum] == -1))
                minimum = i;
        }

        if ( tri[minimum] == -1 ) break;

        cout << paquet[minimum]->getCode() << " : " << tri[minimum] << endl;

		liste_match = paquet[minimum]->comparer(banane);				// matching
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

