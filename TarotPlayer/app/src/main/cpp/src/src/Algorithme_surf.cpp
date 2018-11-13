#include "Algorithme_surf.hpp"

using namespace cv;
using namespace std;

const int GOOD_PTS_MAX = 50;
const float GOOD_PORTION = 0.15f;


Algorithme_surf::Algorithme_surf(string const& fichier): Histogramme(fichier), rows(image.rows), cols(image.cols), descripteurs(0), points(0), refer_vector_points(0)
{}

Algorithme_surf::Algorithme_surf(string code, int occurences): Histogramme(code, false), refer_vector_points(0)
{
	descripteurs = new Mat(occurences, 64, CV_32F);
	points = (KeyPoint*) malloc(occurences * sizeof(KeyPoint));
	for ( int i = 0 ; i < occurences ; i ++ )
	{
		cache->lire_point(points+i);
		cache->lire_desc(descripteurs->ptr<float>(i));
		if ( (points+i)->pt.x > cols ) cols = (points+i)->pt.x;
		if ( (points+i)->pt.y > rows ) rows = (points+i)->pt.y;
	}
}

Algorithme_surf::~Algorithme_surf()
{
	//if(points) free(points);
}

void Algorithme_surf::calcul_descripteurs()
{
	static Ptr<SURF> detector = SURF::create();

	refer_vector_points = new vector<KeyPoint>();
	descripteurs = new Mat();

	if ( image.channels() != 1 ) extractChannel(image, image, 2);
	detector->detectAndCompute(image, Mat(), *refer_vector_points, *descripteurs);
	points = refer_vector_points->data();

}

vector<DMatch>* Algorithme_surf::comparer(Algorithme_surf autre)
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
	cache->insertion_points(points, descripteurs->rows);
	cache->insertion_desc(*descripteurs);
	return descripteurs->rows;
}

bool Algorithme_surf::investigation(Algorithme_surf echantillon, vector<DMatch> const& abricot)
{
	vector<Point2f> source, destination;
	vector<uchar> mask(abricot.size());
	Mat homo;

	for ( int w = 0 ; w < abricot.size() ; w ++ )
	{
		source.push_back( echantillon.getPoint( abricot[w].queryIdx ).pt );
		destination.push_back( points[ abricot[w].trainIdx ].pt );
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
	cout << endl << "cadre :      ( cols = " << cols << " - rows : " << rows << " )" << endl;
	for ( int w = 0 ; w < 4 ; w ++ ) cout << scene[w].x << " - " << scene[w].y << endl;
	#endif

	return vraisemblance_rectangle(scene.data(), rows, cols);
}


