/**
 * main_test.c
 * Written by Michael Barker and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */

#include "hdr_histogram_test.h"
#include "hdr_histogram_log_test.h"
#include "hdr_dbl_histogram_test.h"

int main(int argc, char **argv)
{
    hdr_histogram_run_tests();
    hdr_histogram_log_run_tests();
    hdr_dbl_histogram_run_tests();
}
