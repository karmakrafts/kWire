#ifdef __cplusplus
#define TEST_CALL extern "C"
#else
#define TEST_CALL
#endif

#define TEST_EXPORT __attribute__((visibility("default")))
#define TEST_API TEST_CALL TEST_EXPORT

TEST_API int testlib_test1() {
	return 1337;
}

TEST_API int testlib_test2(int i1, int i2) {
	return i1 == i2;
}
