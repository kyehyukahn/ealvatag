package ealvatag.utils;

/**
 * Along the line of Guava's Preconditions
 * <p>
 * Created by eric on 1/13/17.
 */
public class Check {

    public static <T> T checkArgNotNull(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        } else {
            return reference;
        }
    }

    public static <T> T checkArgNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

    public static <T> T checkArgNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        } else {
            return reference;
        }
    }


    public static <T> T checkVarArg0NotNull(T[] reference) {
        if (reference == null || reference[0] == null) {
            throw new IllegalArgumentException();
        } else {
            return reference[0];
        }
    }

    public static <T> T checkVarArg0NotNull(T[] reference, Object errorMessage) {
        if (reference == null || reference[0] == null) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        } else {
            return reference[0];
        }
    }

    public static <T> T checkVarArg0NotNull(T[] reference, String errorMessageTemplate, Object... errorMessageArgs) {
        if (reference == null || reference[0] == null) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        } else {
            return reference[0];
        }
    }

}