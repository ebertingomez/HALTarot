#include "Carte.hpp"
#include "Isolation.hpp"

#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <signal.h>

using namespace std;
using namespace cv;

extern Carte* paquet_carte;
Point2f* points;
extern Plage_cartes homographie;
string chemin_carte;

bool mode_test, mode_demi_image;
extern float sous_echantillon_isolation;
extern float sous_echantillon_reconnaissance;
extern Mat image;

extern string chemin_absolu;

int main (int argc, char** argv)
{
	points = new Point2f[4];
	mode_demi_image = false;

	ofstream log (chemin_absolu + "log.txt");// TODO: delete
	if ( string(argv[1]) == "touche")
	{
		imshow("lkjbé", Mat(5,5, CV_8U));
		log << waitKey(0) << endl;
	}

	chemin_carte = string(argv[2]);

	if ( string(argv[1]) == "cherche" )
	{
		paquet_carte = new Carte();
		log << "------------>  " << paquet_carte->analyse(argv[2]) << endl;
		delete paquet_carte;
	}
	else if ( string(argv[1]) == "table" )
	{
		isolation_dijkstra(string(argv[2]));
		homographie.sauver_images();
	}
	else if ( string(argv[1]) == "demi_table" )
	{
		mode_demi_image = true;
		isolation_dijkstra(string(argv[2]));
		homographie.sauver_images();
	}
	else if ( string(argv[1]) == "découpe" )
	{
		string fichier(argv[2]);
		isolation_rectangle(fichier);

		homographie.sauver_images();
	}

	delete[] points;
	log.close();
	return 0;
}
