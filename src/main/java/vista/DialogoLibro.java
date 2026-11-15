package vista;

import modelo.Categoria;
import modelo.Libro;
import servicio.BibliotecaException;
import servicio.CatalogoService;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.FlowLayout;

/**
 * Diálogo para crear o editar un libro. Toda la interfaz está construida en código.
 *
 * Nuevo libro:  new DialogoLibro(ventana, true).setVisible(true);
 * Editar libro: dialogo.setLibro(libro) ANTES de setVisible(true).
 * Después de cerrarse, isGuardado() dice si el usuario guardó los cambios.
 */
public class DialogoLibro extends JDialog {

    private final CatalogoService svcCatalogo = new CatalogoService();

    private final JTextField txtTitulo = new JTextField(24);
    private final JTextField txtAutor = new JTextField(24);
    private final JTextField txtIsbn = new JTextField(24);
    private final JComboBox<Categoria> cmbCategoriaLibro = new JComboBox<>();
    private final JSpinner spnEjemplares = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    // idLibro = 0 significa "libro nuevo"; si se llama a setLibro(), es edición
    private Libro libro = new Libro();

    // La ventana principal lo consulta al cerrarse el diálogo
    private boolean guardado = false;

    public DialogoLibro(Frame parent, boolean modal) {
        super(parent, "Nuevo libro", modal);
        construirInterfaz();
        cargarCategorias();

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);   // Enter = Guardar

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setContentPane(contenido);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        agregarFila(contenido, c, 0, "Título:", txtTitulo);
        agregarFila(contenido, c, 1, "Autor:", txtAutor);
        agregarFila(contenido, c, 2, "ISBN:", txtIsbn);
        agregarFila(contenido, c, 3, "Categoría:", cmbCategoriaLibro);
        agregarFila(contenido, c, 4, "Ejemplares:", spnEjemplares);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        contenido.add(botones, c);
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String texto,
            java.awt.Component campo) {
        c.gridy = fila;
        c.gridwidth = 1;
        c.gridx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(texto), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private void cargarCategorias() {
        try {
            DefaultComboBoxModel<Categoria> modelo = new DefaultComboBoxModel<>();
            for (Categoria cat : svcCatalogo.listarCategorias()) {
                modelo.addElement(cat);
            }
            cmbCategoriaLibro.setModel(modelo);
        } catch (BibliotecaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Se llama ANTES de mostrar el diálogo cuando se quiere editar un libro existente. */
    public void setLibro(Libro l) {
        this.libro = l;
        setTitle("Editar libro");
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn() == null ? "" : l.getIsbn());
        spnEjemplares.setValue(l.getEjemplaresTotales());

        // Seleccionar en el combo la categoría que tiene el libro
        for (int i = 0; i < cmbCategoriaLibro.getItemCount(); i++) {
            if (cmbCategoriaLibro.getItemAt(i).getIdCategoria() == l.getIdCategoria()) {
                cmbCategoriaLibro.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {
        libro.setTitulo(txtTitulo.getText().trim());
        libro.setAutor(txtAutor.getText().trim());
        libro.setIsbn(txtIsbn.getText().trim());
        Categoria cat = (Categoria) cmbCategoriaLibro.getSelectedItem();
        libro.setIdCategoria(cat == null ? 0 : cat.getIdCategoria());
        libro.setEjemplaresTotales((Integer) spnEjemplares.getValue());

        try {
            svcCatalogo.guardarLibro(libro);   // valida y decide si inserta o actualiza
            guardado = true;
            dispose();                          // cierra el diálogo
        } catch (BibliotecaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
