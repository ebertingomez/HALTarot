#ifndef _ANNEXE_H
#define _ANNEXE_H

#include "Cache.hpp"
#include <utility>
#include <semaphore.h>

#define DETERMINANT(a,b,c,d) (((a).x-(b).x)*((c).y-(d).y) - ((a).y-(b).y)*((c).x-(d).x))
#define SCALAIRE(p,q,r) (((p).x-(q).x)*((q).x-(r).x) + ((p).y-(q).y)*((q).y-(r).y))
#define DISTANCE(p,q)   (((p).x-(q).x)*((p).x-(q).x) + ((p).y-(q).y)*((p).y-(q).y))
#define CARRE(p)		((p)*(p))
#define MAX4(a,b,c,d) (max((int)(a),max((int)(b),max((int)(c),(int)(d)))))
#define MIN4(a,b,c,d) (min((a),min((b),min((c),(d)))))
#define EXTREMITE(vec) Point((vec)[2],(vec)[3])
#define ORIGINE(vec) Point((vec)[0],(vec)[1])
#define AIRE(a,b,c,d) ((sqrt(DISTANCE((a),(b)))+sqrt(DISTANCE((c),(d))))*(sqrt(DISTANCE((a),(d)))+sqrt(DISTANCE((b),(c))))/4)

#define ROWS getImage().rows
#define COLS getImage().cols

#define DEBUG_

pair<float, float> regression(vector<Vec4i> lignes);
Point2f intersection_segment(Vec4f const& a, Vec4f const& b);

Point2f intersection_segment(Vec4f const& a, Vec4f const& b);
Point2f intersection_droites(pair<float,float> d1, pair<float,float> d2 );

Classification classification(string nom, bool simple = false);
string transcription_francais(Classification classe);

float somme_cube(Mat& histo, int i);

bool vraisemblance_rectangle(Point2f* scene, int rows = 0, int cols = 0);

const vector<Point2f> dimensions_choux = { Point2f(0,0),	Point2f(300,0),		Point2f(300,550),	Point2f(0,550) };
const vector<Point2f> dimensions_fleur = { Point2f(300,0),	Point2f(300,550),	Point2f(0,550),		Point2f(0,0) };
const vector<Point2f> dimensions_ruelf = { Point2f(0,0),	Point2f(0,550),		Point2f(300,550),	Point2f(300,0) };
const vector<Point2f> dimensions_xuohc = { Point2f(300,0),	Point2f(0,0), 		Point2f(0,550),		Point2f(300,550) };


class Plage_cartes
{
private:

	vector<Point2f> plages;
	Mat extraire_carte(int indice) const;

public:

	bool ajouter(Point2f* carte);
	vector<Point2f> restitution() { return plages; };
	void sauver_images() const;
	void multiplier_plage(float facteur);

	Mat la_plus_grande();
	void toutes(vector<Mat>&) const;

};

#endif

