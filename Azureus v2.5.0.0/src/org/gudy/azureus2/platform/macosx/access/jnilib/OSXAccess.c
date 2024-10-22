#include <Carbon/Carbon.h>
#include <jni.h>
#include <AEDataModel.h>

#define VERSION "1.01"

/**
 * AEDesc code from SWT, os_structs.c
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 */
typedef struct AEDesc_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID descriptorType, dataHandle;
} AEDesc_FID_CACHE;

AEDesc_FID_CACHE AEDescFc;

void cacheAEDescFields(JNIEnv *env, jobject lpObject)
{
	if (AEDescFc.cached) return;
	AEDescFc.clazz = (*env)->GetObjectClass(env, lpObject);
	AEDescFc.descriptorType = (*env)->GetFieldID(env, AEDescFc.clazz, "descriptorType", "I");
	AEDescFc.dataHandle = (*env)->GetFieldID(env, AEDescFc.clazz, "dataHandle", "I");
	AEDescFc.cached = 1;
}

AEDesc *getAEDescFields(JNIEnv *env, jobject lpObject, AEDesc *lpStruct)
{
	if (!AEDescFc.cached) cacheAEDescFields(env, lpObject);
	lpStruct->descriptorType = (DescType)(*env)->GetIntField(env, lpObject, AEDescFc.descriptorType);
	lpStruct->dataHandle = (AEDataStorage)(*env)->GetIntField(env, lpObject, AEDescFc.dataHandle);
	return lpStruct;
}

void setAEDescFields(JNIEnv *env, jobject lpObject, AEDesc *lpStruct)
{
	if (!AEDescFc.cached) cacheAEDescFields(env, lpObject);
	(*env)->SetIntField(env, lpObject, AEDescFc.descriptorType, (jint)lpStruct->descriptorType);
	(*env)->SetIntField(env, lpObject, AEDescFc.dataHandle, (jint)lpStruct->dataHandle);
}

JNIEXPORT jint JNICALL Java_org_gudy_azureus2_platform_macosx_access_jnilib_OSXAccess_AEGetParamDesc
	(JNIEnv *env, jclass that, jint theAppleEvent, jint theAEKeyword, 
	 jint desiredType, jobject result)
{
	AEDesc _result, *lpresult=NULL;
	
	jint rc = 0;

	if (result) if ((lpresult = getAEDescFields(env, result, &_result)) == NULL) goto fail;
	
	rc = (jint)AEGetParamDesc((const AppleEvent *)theAppleEvent, 
	  (AEKeyword)theAEKeyword,
	  (DescType)desiredType,
	  (AEDescList *)lpresult);

fail:
	if (result && lpresult) setAEDescFields(env, result, lpresult);

	return rc;
}

JNIEXPORT jstring JNICALL 
Java_org_gudy_azureus2_platform_macosx_access_jnilib_OSXAccess_getVersion(
	JNIEnv		*env,
	jclass		cla )
{
	jstring	result =(*env)->NewStringUTF(env, (char *)VERSION);

	return( result );
}
