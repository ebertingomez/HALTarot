#include <jni.h>
#include <string>
#include "Carte.hpp"

std::string chemin_absolu;
/*
extern "C"
JNIEXPORT jstring

JNICALL
Java_fr_telecom_1paristech_pact42_tarot_tarotplayer_CardGame_CardAcquisition_analyzeFromJNI(
        JNIEnv *env,
        jobject /* this *//*,
        std::string path) {
    chemin_absolu = path + "/";
    Carte paquet;
    std::string res = paquet.analyse(path);
    return env->NewStringUTF(res.c_str());
}
*/
extern "C"
JNIEXPORT jstring

JNICALL
Java_fr_telecom_1paristech_pact42_tarot_tarotplayer_CardGame_CardAcquisition_analyzeFromJNI(
        JNIEnv *env, jclass type, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    chemin_absolu = path;
    chemin_absolu += "/";
    Carte paquet;
    std::string res = paquet.analyse(path);

    env->ReleaseStringUTFChars(path_, path);

    return env->NewStringUTF(res.c_str());
}