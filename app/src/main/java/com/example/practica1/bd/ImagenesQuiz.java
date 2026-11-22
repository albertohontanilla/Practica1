package com.example.practica1.bd;

import androidx.annotation.DrawableRes;

import com.example.practica1.R;

/**
 * Traduce las claves de imagen guardadas en la base de datos a recursos drawable.
 * Se guardan claves de texto (y no ids de recurso) porque los ids pueden cambiar
 * entre compilaciones, mientras que la base de datos persiste en el dispositivo.
 */
public final class ImagenesQuiz {

    public static final String JARRA = "jarra";
    public static final String LOGO_MAHOU = "logo_mahou";
    public static final String LOGO_ESTRELLA_GALICIA = "logo_estrella_galicia";
    public static final String LOGO_CRUZCAMPO = "logo_cruzcampo";
    public static final String LOGO_SAN_MIGUEL = "logo_san_miguel";

    private ImagenesQuiz() {
    }

    @DrawableRes
    public static int obtener(String clave) {
        if (clave == null) {
            return R.drawable.jarra_cerveza;
        }
        switch (clave) {
            case LOGO_MAHOU:
                return R.drawable.logo_mahou;
            case LOGO_ESTRELLA_GALICIA:
                return R.drawable.logo_estrella_galicia;
            case LOGO_CRUZCAMPO:
                return R.drawable.logo_cruzcampo;
            case LOGO_SAN_MIGUEL:
                return R.drawable.logo_san_miguel;
            case JARRA:
            default:
                return R.drawable.jarra_cerveza;
        }
    }
}
