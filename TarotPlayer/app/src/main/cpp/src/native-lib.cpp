#include <jni.h>
#include <string>
#include "Carte.hpp"

extern "C"
JNIEXPORT jstring

JNICALL
Java_fr_telecom_1paristech_pact42_tarot_tarotplayer_CardGame_CardAcquisition_analyzeFromJNI(
        JNIEnv *env,
        jobject /* this */,
        std::string path) {
    Carte paquet;
    std::string res = paquet.analyse(path);
    return env->NewStringUTF(res.c_str());
}