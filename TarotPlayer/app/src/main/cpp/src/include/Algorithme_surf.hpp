#ifndef _SURF_H
#define _SURF_H

#include "Cache.hpp"
#include "Histogramme.hpp"



using namespace std;
using namespace cv;
using namespace cv::xfeatures2d;

class Algorithme_surf : public Histogramme
{
	private:

	Mat* descripteurs;
	KeyPoint* points_carac;
	vector<KeyPoint>* refer_vector_points;
    int rows = -1;
    int cols = -1;

	public:
	Algorithme_surf(string const& fichier);
    Algorithme_surf(Mat const& carte);
	Algorithme_surf(string code, int occurences);

	~Algorithme_surf();

	void calcul_descripteurs(bool haut = true);
	bool investigation(Algorithme_surf echantillon, vector<DMatch> const& abricot);

	vector<KeyPoint> getPoints() { return vector<KeyPoint>(points_carac, points_carac + descripteurs->rows); }
	Mat getDescripteurs() { return *descripteurs; }
	KeyPoint getPoint(int i) { return points_carac[i]; }

	vector<DMatch>* comparer(Algorithme_surf autre);
	short vers_fichier();

    int getRows() { return rows; }
    int getCols() { return cols; }
};

#endif
