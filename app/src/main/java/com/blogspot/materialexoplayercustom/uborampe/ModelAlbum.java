package com.blogspot.materialexoplayercustom.uborampe;

public class ModelAlbum {

    String _judul, _subjudul, _genre, _url, _gambarposter, _gambarfull, _deskripsi;

    public ModelAlbum(String judul, String subjudul, String genre, String url, String gambarposter, String gambarfull, String deskripsi) {
        this._judul = judul;
        this._subjudul = subjudul;
        this._genre = genre;
        this._url = url;
        this._gambarposter = gambarposter;
        this._gambarfull = gambarfull;
        this._deskripsi = deskripsi;
    }

    public String get_judul() {
        return _judul;
    }

    public void set_judul(String _judul) {
        this._judul = _judul;
    }

    public String get_subjudul() {
        return _subjudul;
    }

    public void set_subjudul(String _subjudul) {
        this._subjudul = _subjudul;
    }

    public String get_genre() {
        return _genre;
    }

    public void set_genre(String _genre) {
        this._genre = _genre;
    }

    public String get_url() {
        return _url;
    }

    public void set_url(String _url) {
        this._url = _url;
    }

    public String get_gambarposter() {
        return _gambarposter;
    }

    public void set_gambarposter(String _gambarposter) {
        this._gambarposter = _gambarposter;
    }

    public String get_gambarfull() {
        return _gambarfull;
    }

    public void set_gambarfull(String _gambarfull) {
        this._gambarfull = _gambarfull;
    }

    public String get_deskripsi() {
        return _deskripsi;
    }

    public void set_deskripsi(String _deskripsi) {
        this._deskripsi = _deskripsi;
    }

}
