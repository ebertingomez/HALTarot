#include "Algorithme_surf.hpp"

using namespace cv;
using namespace std;

const int GOOD_PTS_MAX = 50;
const float GOOD_PORTION = 0.15f;


Algorithme_surf::Algorithme_surf(string const& fichier): Histogramme(fichier), rows(image.rows), cols(image.cols), descripteurs(0), points_carac(0), refer_vector_points(0)
{}

Algorithme_surf::Algorithme_surf(Mat const& carte): Histogramme(carte, "--")
{}


Algorithme_surf::Algorithme_surf(string code, int occurences): Histogramme(code, false), refer_vector_points(0)					// chargement des descripteurs préenregistrés

{
	descripteurs = new Mat(occurences, 64, CV_32F);
	points_carac = (KeyPoint*) malloc(occurences * sizeof(KeyPoint));
	for ( int i = 0 ; i < occurences ; i ++ )
	{
		cache->lire_point(points_carac+i);
		cache->lire_desc(descripteurs->ptr<float>(i));
		if ( (points_carac+i)->pt.x > cols ) cols = (points_carac+i)->pt.x;
		if ( (points_carac+i)->pt.y > rows ) rows = (points_carac+i)->pt.y;
	}
}

Algorithme_surf::~Algorithme_surf()
{
	//if(points) free(points);
}

void Algorithme_surf::calcul_descripteurs(bool haut)
{
	static Ptr<SURF> detector = SURF::create();

	refer_vector_points = new vector<KeyPoint>();
	descripteurs = new Mat();

	Mat demi_image;
	if (haut)		demi_image = image(Range(0,				image.rows/2), Range::all());
	else 			demi_image = image(Range(image.rows/2,	image.rows	), Range::all());


	if ( image.channels() != 1 ) extractChannel(demi_image, demi_image, 2);
	detector->detectAndCompute(demi_image, Mat(), *refer_vector_points, *descripteurs);
	points_carac = refer_vector_points->data();

}

vector<DMatch>* Algorithme_surf::comparer(Algorithme_surf autre)		// matching par force brute
{
	vector<DMatch> matches;

	BFMatcher matcher;
	matcher.match(*descripteurs, autre.getDescripteurs(), matches);

    sort(matches.begin(), matches.end());
    vector<DMatch>* good_matches = new vector<DMatch>();
    double minDist = matches.front().distance;
    double maxDist = matches.back().distance;

    const int ptsPairs = min(GOOD_PTS_MAX, (int)(matches.size() * GOOD_PORTION));
    for( int i = 0; i < ptsPairs; i++ )
    {
        good_matches->push_back( matches[i] );
    }

    return good_matches;
}

short Algorithme_surf::vers_fichier()
{
	int j;
	cache->insertion_points(points_carac, descripteurs->rows);
	cache->insertion_desc(descripteurs);
	return descripteurs->rows;
}

bool Algorithme_surf::investigation(Algorithme_surf echantillon, vector<DMatch> const& abricot)			// matching par recherche d'homographie
{
	vector<Point2f> source, destination;
	vector<uchar> mask(abricot.size());
	Mat homo;

	for ( int w = 0 ; w < abricot.size() ; w ++ )
	{
		source.push_back( echantillon.getPoint( abricot[w].queryIdx ).pt );
		destination.push_back( points_carac[ abricot[w].trainIdx ].pt );
	}
	homo = findHomography(source, destination, RANSAC, 3, mask);

	vector<Point2f> obj_corners(4);
	obj_corners[0] = Point( 0, 0 );
	obj_corners[1] = Point( echantillon.getCols(), 0 );
	obj_corners[2] = Point( echantillon.getCols(), echantillon.getRows() );
	obj_corners[3] = Point( 0, echantillon.getRows() );
	vector<Point2f> scene(4);

	perspectiveTransform( obj_corners, scene, homo);

	#ifdef DEBUG_HOMOGRAPHIE
	ofstream log (chemin_absolu + "log.txt");
	log << endl << "cadre :      ( cols = " << cols << " - rows : " << rows << " )" << endl;
	for ( int w = 0 ; w < 4 ; w ++ ) log << scene[w].x << " - " << scene[w].y << endl;
	log.close();
	#endif

	return vraisemblance_rectangle(scene.data(), rows, cols);
}


