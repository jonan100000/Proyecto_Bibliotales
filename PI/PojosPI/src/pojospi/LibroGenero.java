/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

/**
 *
 * @author DosherGG
 */
public class LibroGenero {
    private Libro libro;
    private Genero genero;

    public LibroGenero() {
    }

    public LibroGenero(Libro libro, Genero genero) {
        this.libro = libro;
        this.genero = genero;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "LibroGenero{" + "libro=" + libro + ", genero=" + genero + '}';
    }
    
    
}
