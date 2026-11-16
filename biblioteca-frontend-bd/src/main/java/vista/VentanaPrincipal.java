package vista;

import modelo.Categoria;
import modelo.Libro;
import modelo.Multa;
import modelo.Prestamo;
import modelo.Usuario;
import servicio.AlertaService;
import servicio.BibliotecaException;
import servicio.CatalogoService;
import servicio.MultaService;
import servicio.PrestamoService;
import servicio.RecomendacionService;
import servicio.UsuarioService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal de la Biblioteca Inteligente.
 *
 * Toda la interfaz está construida en código (sin el diseñador de NetBeans),
 * por lo que esta clase se puede copiar tal cual al paquete "vista".
 * Para ejecutar: clic derecho sobre este archivo > Run File.
 */
public class VentanaPrincipal extends JFrame {

    // ---------- Servicios del back end ----------
    private final CatalogoService svcCatalogo = new CatalogoService();
    private final PrestamoService svcPrestamos = new PrestamoService();
    private final MultaService svcMultas = new MultaService();
    private final AlertaService svcAlertas = new AlertaService();
    private final RecomendacionService svcRecomendaciones = new RecomendacionService();
    private final UsuarioService svcUsuarios = new UsuarioService();

    // ---------- Componentes (mismos nombres que en la guía del diseño) ----------
    private final JTabbedPane tabPrincipal = new JTabbedPane();

    // Pestaña Catálogo
    private final JPanel pnlCatalogo = new JPanel(new BorderLayout(8, 8));
    private final JTextField txtBuscar = new JTextField(22);
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JTable tblLibros = new JTable();
    private final JList<Libro> lstRecomendados = new JList<>();
    private final JButton btnNuevoLibro = new JButton("Nuevo libro");
    private final JButton btnEditarLibro = new JButton("Editar");
    private final JButton btnEliminarLibro = new JButton("Eliminar");

    // Pestaña Préstamos
    private final JPanel pnlPrestamos = new JPanel(new BorderLayout(8, 8));
    private final JComboBox<Usuario> cmbUsuario = new JComboBox<>();
    private final JComboBox<Libro> cmbLibro = new JComboBox<>();
    private final JButton btnPrestar = new JButton("Prestar");
    private final JTable tblPrestamos = new JTable();
    private final JButton btnDevolver = new JButton("Devolver libro seleccionado");

    // Pestaña Alertas y multas
    private final JPanel pnlAlertas = new JPanel(new GridLayout(2, 1, 8, 8));
    private final JList<String> lstAlertas = new JList<>();
    private final JButton btnActualizarAlertas = new JButton("Actualizar");
    private final JTable tblMultas = new JTable();
    private final JLabel lblTotalMultas = new JLabel("Total: $0.00");
    private final JButton btnPagarMulta = new JButton("Marcar como pagada");

    // Pestaña Usuarios
    private final JPanel pnlUsuarios = new JPanel(new BorderLayout(8, 8));
    private final JTextField txtNombreUsuario = new JTextField(14);
    private final JTextField txtCorreo = new JTextField(16);
    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"alumno", "docente", "admin"});
    private final JButton btnRegistrarUsuario = new JButton("Registrar");
    private final JTable tblUsuarios = new JTable();

    // Evita que los combos disparen su evento mientras los llenamos por código
    private boolean cargando = false;

    public VentanaPrincipal() {
        super("Biblioteca Inteligente");
        construirInterfaz();
        conectarEventos();
    }

    // =====================================================================
    //  CONSTRUCCIÓN DE LA INTERFAZ
    // =====================================================================
    private void construirInterfaz() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tablas: columnas fijas, no editables, una fila seleccionable a la vez
        tblLibros.setModel(crearModelo("ID", "Título", "Autor", "Categoría", "Disponibles"));
        tblPrestamos.setModel(crearModelo("ID", "Libro", "Usuario", "Préstamo", "Límite", "Estado"));
        tblMultas.setModel(crearModelo("ID", "Usuario", "Libro", "Monto"));
        tblUsuarios.setModel(crearModelo("ID", "Nombre", "Correo", "Tipo"));
        for (JTable t : new JTable[]{tblLibros, tblPrestamos, tblMultas, tblUsuarios}) {
            t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            t.setAutoCreateRowSorter(true);   // clic en el encabezado ordena
            t.setRowHeight(24);
            anchoColumna(t, 0, 50);
        }

        // ----- Pestaña Catálogo -----
        pnlCatalogo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBusqueda.add(new JLabel("Buscar:"));
        pnlBusqueda.add(txtBuscar);
        pnlBusqueda.add(new JLabel("Categoría:"));
        cmbCategoria.setPreferredSize(new Dimension(170, 26));
        pnlBusqueda.add(cmbCategoria);
        pnlCatalogo.add(pnlBusqueda, BorderLayout.NORTH);

        pnlCatalogo.add(new JScrollPane(tblLibros), BorderLayout.CENTER);

        JPanel pnlRecomendados = new JPanel(new BorderLayout());
        pnlRecomendados.setBorder(BorderFactory.createTitledBorder("Recomendados"));
        pnlRecomendados.setPreferredSize(new Dimension(260, 0));
        pnlRecomendados.add(new JScrollPane(lstRecomendados), BorderLayout.CENTER);
        pnlCatalogo.add(pnlRecomendados, BorderLayout.EAST);

        JPanel pnlBotonesCatalogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBotonesCatalogo.add(btnNuevoLibro);
        pnlBotonesCatalogo.add(btnEditarLibro);
        pnlBotonesCatalogo.add(btnEliminarLibro);
        pnlCatalogo.add(pnlBotonesCatalogo, BorderLayout.SOUTH);

        // ----- Pestaña Préstamos -----
        pnlPrestamos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlNuevoPrestamo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlNuevoPrestamo.setBorder(BorderFactory.createTitledBorder("Nuevo préstamo"));
        cmbUsuario.setPreferredSize(new Dimension(200, 26));
        cmbLibro.setPreferredSize(new Dimension(300, 26));
        pnlNuevoPrestamo.add(new JLabel("Usuario:"));
        pnlNuevoPrestamo.add(cmbUsuario);
        pnlNuevoPrestamo.add(new JLabel("Libro:"));
        pnlNuevoPrestamo.add(cmbLibro);
        pnlNuevoPrestamo.add(btnPrestar);
        pnlPrestamos.add(pnlNuevoPrestamo, BorderLayout.NORTH);

        pnlPrestamos.add(new JScrollPane(tblPrestamos), BorderLayout.CENTER);

        JPanel pnlBotonesPrestamo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBotonesPrestamo.add(btnDevolver);
        pnlPrestamos.add(pnlBotonesPrestamo, BorderLayout.SOUTH);

        // ----- Pestaña Alertas y multas -----
        pnlAlertas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Las alertas vencidas se pintan en rojo
        lstAlertas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean seleccionado, boolean foco) {
                Component c = super.getListCellRendererComponent(list, value, index, seleccionado, foco);
                if (!seleccionado && String.valueOf(value).startsWith("VENCIDO")) {
                    c.setForeground(new Color(180, 30, 30));
                }
                return c;
            }
        });

        JPanel pnlListaAlertas = new JPanel(new BorderLayout(4, 4));
        pnlListaAlertas.setBorder(BorderFactory.createTitledBorder("Alertas"));
        pnlListaAlertas.add(new JScrollPane(lstAlertas), BorderLayout.CENTER);
        JPanel pnlBotonAlertas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlBotonAlertas.add(btnActualizarAlertas);
        pnlListaAlertas.add(pnlBotonAlertas, BorderLayout.SOUTH);

        JPanel pnlMultas = new JPanel(new BorderLayout(4, 4));
        pnlMultas.setBorder(BorderFactory.createTitledBorder("Multas pendientes"));
        pnlMultas.add(new JScrollPane(tblMultas), BorderLayout.CENTER);
        JPanel pnlBotonesMultas = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pnlBotonesMultas.add(lblTotalMultas);
        pnlBotonesMultas.add(btnPagarMulta);
        pnlMultas.add(pnlBotonesMultas, BorderLayout.SOUTH);

        pnlAlertas.add(pnlListaAlertas);
        pnlAlertas.add(pnlMultas);

        // ----- Pestaña Usuarios -----
        pnlUsuarios.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlNuevoUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlNuevoUsuario.setBorder(BorderFactory.createTitledBorder("Nuevo usuario"));
        pnlNuevoUsuario.add(new JLabel("Nombre:"));
        pnlNuevoUsuario.add(txtNombreUsuario);
        pnlNuevoUsuario.add(new JLabel("Correo:"));
        pnlNuevoUsuario.add(txtCorreo);
        pnlNuevoUsuario.add(new JLabel("Tipo:"));
        pnlNuevoUsuario.add(cmbTipo);
        pnlNuevoUsuario.add(btnRegistrarUsuario);
        pnlUsuarios.add(pnlNuevoUsuario, BorderLayout.NORTH);
        pnlUsuarios.add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);

        // ----- Pestañas -----
        tabPrincipal.addTab("Catálogo", pnlCatalogo);
        tabPrincipal.addTab("Préstamos", pnlPrestamos);
        tabPrincipal.addTab("Alertas y multas", pnlAlertas);
        tabPrincipal.addTab("Usuarios", pnlUsuarios);
        add(tabPrincipal, BorderLayout.CENTER);
    }

    // Crea un modelo de tabla con las columnas dadas y sin celdas editables
    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void anchoColumna(JTable tabla, int columna, int ancho) {
        tabla.getColumnModel().getColumn(columna).setMaxWidth(ancho);
        tabla.getColumnModel().getColumn(columna).setPreferredWidth(ancho);
    }

    // =====================================================================
    //  EVENTOS
    // =====================================================================
    private void conectarEventos() {

        // EVENTO (WindowListener): al abrir la ventana se carga todo y se muestran las alertas
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                alAbrirVentana();
            }
        });

        // EVENTO (DocumentListener): la búsqueda filtra la tabla mientras se escribe
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cargarLibros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cargarLibros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cargarLibros();
            }
        });

        // EVENTO (ActionListener del combo): filtrar por categoría
        cmbCategoria.addActionListener(e -> {
            if (!cargando) {
                cargarLibros();
            }
        });

        // EVENTO (ListSelectionListener): al seleccionar un libro se muestran los recomendados
        tblLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarRecomendados();
            }
        });

        // EVENTO (ChangeListener): al cambiar de pestaña se refresca su contenido
        tabPrincipal.addChangeListener(e -> refrescarPestanaActual());

        // EVENTO (Timer): cada 60 segundos se refrescan las alertas
        new javax.swing.Timer(60000, e -> {
            cargarAlertas();
            cargarMultas();
        }).start();

        // EVENTOS (ActionListener de los botones)
        btnNuevoLibro.addActionListener(e -> abrirDialogoLibro(null));
        btnEditarLibro.addActionListener(e -> editarLibro());
        btnEliminarLibro.addActionListener(e -> eliminarLibro());
        btnPrestar.addActionListener(e -> prestar());
        btnDevolver.addActionListener(e -> devolver());
        btnActualizarAlertas.addActionListener(e -> {
            cargarAlertas();
            cargarMultas();
        });
        btnPagarMulta.addActionListener(e -> pagarMulta());
        btnRegistrarUsuario.addActionListener(e -> registrarUsuario());
    }

    private void alAbrirVentana() {
        // Si no hay conexión a la base de datos se avisa una sola vez y no se intenta cargar más
        if (!cargarCategorias()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con la base de datos.\n"
                    + "Revise que MariaDB/MySQL esté encendido, que exista la base \"biblioteca\"\n"
                    + "y que usuario y contraseña en dao/Conexion.java sean correctos.",
                    "Sin conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }
        cargarUsuarios();
        cargarLibros();
        cargarComboLibros();
        cargarPrestamos();
        cargarAlertas();
        cargarMultas();

        // Si hay alertas, se avisa y se muestra esa pestaña
        int total = lstAlertas.getModel().getSize();
        if (total > 0) {
            tabPrincipal.setSelectedComponent(pnlAlertas);
            JOptionPane.showMessageDialog(this,
                    "Hay " + total + " alerta(s) de préstamos por vencer o vencidos.",
                    "Alertas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refrescarPestanaActual() {
        Component actual = tabPrincipal.getSelectedComponent();
        if (actual == pnlCatalogo) {
            cargarLibros();
        } else if (actual == pnlPrestamos) {
            cargarComboLibros();
            cargarPrestamos();
        } else if (actual == pnlAlertas) {
            cargarAlertas();
            cargarMultas();
        } else if (actual == pnlUsuarios) {
            cargarUsuarios();
        }
    }

    // =====================================================================
    //  ACCIONES DE LOS BOTONES
    // =====================================================================
    private void abrirDialogoLibro(Libro libro) {
        DialogoLibro dialogo = new DialogoLibro(this, true);
        if (libro != null) {
            dialogo.setLibro(libro);
        }
        dialogo.setVisible(true);   // se queda aquí hasta que se cierre el diálogo
        if (dialogo.isGuardado()) {
            cargarLibros();
            cargarComboLibros();
        }
    }

    private void editarLibro() {
        int idLibro = idSeleccionado(tblLibros, "Seleccione un libro de la tabla para editarlo.");
        if (idLibro == -1) {
            return;
        }
        try {
            abrirDialogoLibro(svcCatalogo.obtenerLibro(idLibro));
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void eliminarLibro() {
        int idLibro = idSeleccionado(tblLibros, "Seleccione un libro de la tabla para eliminarlo.");
        if (idLibro == -1) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el libro seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            svcCatalogo.eliminarLibro(idLibro);
            cargarLibros();
            cargarComboLibros();
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void prestar() {
        Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();
        Libro libro = (Libro) cmbLibro.getSelectedItem();
        if (usuario == null || libro == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario y un libro.");
            return;
        }
        try {
            String mensaje = svcPrestamos.prestar(usuario.getIdUsuario(), libro.getIdLibro());
            JOptionPane.showMessageDialog(this, mensaje);
            refrescarTodo();
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void devolver() {
        int idPrestamo = idSeleccionado(tblPrestamos, "Seleccione el préstamo que se va a devolver.");
        if (idPrestamo == -1) {
            return;
        }
        try {
            String mensaje = svcPrestamos.devolver(idPrestamo);
            JOptionPane.showMessageDialog(this, mensaje);
            refrescarTodo();
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void pagarMulta() {
        int idMulta = idSeleccionado(tblMultas, "Seleccione la multa que se va a pagar.");
        if (idMulta == -1) {
            return;
        }
        try {
            svcMultas.pagar(idMulta);
            cargarMultas();
            cargarAlertas();
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void registrarUsuario() {
        try {
            svcUsuarios.registrarUsuario(txtNombreUsuario.getText(), txtCorreo.getText(),
                    (String) cmbTipo.getSelectedItem());
            txtNombreUsuario.setText("");
            txtCorreo.setText("");
            cargarUsuarios();
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    // =====================================================================
    //  AYUDAS
    // =====================================================================
    private void mostrarError(BibliotecaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // Devuelve el ID (primera columna) de la fila seleccionada, o -1 si no hay selección
    private int idSeleccionado(JTable tabla, String mensajeSiNoHay) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, mensajeSiNoHay);
            return -1;
        }
        return ((Number) tabla.getValueAt(fila, 0)).intValue();
    }

    // Recarga todo lo que cambia después de un préstamo o una devolución
    private void refrescarTodo() {
        cargarLibros();
        cargarComboLibros();
        cargarPrestamos();
        cargarAlertas();
        cargarMultas();
    }

    // =====================================================================
    //  MÉTODOS DE CARGA (llenan tablas, listas y combos desde la base de datos)
    // =====================================================================

    /** Devuelve false si no se pudo consultar (por ejemplo, sin conexión a la base de datos). */
    private boolean cargarCategorias() {
        cargando = true;
        try {
            DefaultComboBoxModel<Categoria> modelo = new DefaultComboBoxModel<>();
            modelo.addElement(new Categoria(0, "Todas"));   // el 0 significa "sin filtro"
            for (Categoria c : svcCatalogo.listarCategorias()) {
                modelo.addElement(c);
            }
            cmbCategoria.setModel(modelo);
            return true;
        } catch (BibliotecaException ex) {
            return false;
        } finally {
            cargando = false;
        }
    }

    private void cargarLibros() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblLibros.getModel();
            modelo.setRowCount(0);
            Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
            int idCategoria = (cat == null) ? 0 : cat.getIdCategoria();
            for (Libro l : svcCatalogo.buscar(txtBuscar.getText(), idCategoria)) {
                modelo.addRow(new Object[]{
                    l.getIdLibro(), l.getTitulo(), l.getAutor(), l.getNombreCategoria(),
                    l.getEjemplaresDisponibles() + "/" + l.getEjemplaresTotales()
                });
            }
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    // Combo de la pestaña Préstamos: solo libros con ejemplares disponibles
    private void cargarComboLibros() {
        try {
            Libro anterior = (Libro) cmbLibro.getSelectedItem();
            DefaultComboBoxModel<Libro> modelo = new DefaultComboBoxModel<>();
            for (Libro l : svcCatalogo.listarLibros()) {
                if (l.getEjemplaresDisponibles() > 0) {
                    modelo.addElement(l);
                    if (anterior != null && anterior.getIdLibro() == l.getIdLibro()) {
                        modelo.setSelectedItem(l);   // conserva la selección
                    }
                }
            }
            cmbLibro.setModel(modelo);
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    // Llena el combo de usuarios (pestaña Préstamos) y la tabla de la pestaña Usuarios
    private void cargarUsuarios() {
        try {
            Usuario anterior = (Usuario) cmbUsuario.getSelectedItem();
            DefaultComboBoxModel<Usuario> combo = new DefaultComboBoxModel<>();
            DefaultTableModel tabla = (DefaultTableModel) tblUsuarios.getModel();
            tabla.setRowCount(0);
            for (Usuario u : svcUsuarios.listarUsuarios()) {
                combo.addElement(u);
                if (anterior != null && anterior.getIdUsuario() == u.getIdUsuario()) {
                    combo.setSelectedItem(u);
                }
                tabla.addRow(new Object[]{
                    u.getIdUsuario(), u.getNombre(),
                    u.getCorreo() == null ? "" : u.getCorreo(), u.getTipo()
                });
            }
            cmbUsuario.setModel(combo);
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void cargarPrestamos() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblPrestamos.getModel();
            modelo.setRowCount(0);
            for (Prestamo p : svcPrestamos.listarActivos()) {
                String estado = p.estaVencido()
                        ? "VENCIDO (" + p.diasRetraso() + " d)" : "Activo";
                modelo.addRow(new Object[]{
                    p.getIdPrestamo(), p.getTituloLibro(), p.getNombreUsuario(),
                    p.getFechaPrestamo(), p.getFechaLimite(), estado
                });
            }
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void cargarAlertas() {
        try {
            DefaultListModel<String> modelo = new DefaultListModel<>();
            for (String alerta : svcAlertas.obtenerAlertas()) {
                modelo.addElement(alerta);
            }
            lstAlertas.setModel(modelo);
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void cargarMultas() {
        try {
            DefaultTableModel modelo = (DefaultTableModel) tblMultas.getModel();
            modelo.setRowCount(0);
            double total = 0;
            for (Multa m : svcMultas.listarPendientes(0)) {   // 0 = de todos los usuarios
                modelo.addRow(new Object[]{
                    m.getIdMulta(), m.getNombreUsuario(), m.getTituloLibro(),
                    String.format(Locale.US, "$%.2f", m.getMonto())
                });
                total += m.getMonto();
            }
            lblTotalMultas.setText(String.format(Locale.US, "Total: $%.2f", total));
        } catch (BibliotecaException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarRecomendados() {
        DefaultListModel<Libro> modelo = new DefaultListModel<>();
        int fila = tblLibros.getSelectedRow();
        if (fila != -1) {
            int idLibro = ((Number) tblLibros.getValueAt(fila, 0)).intValue();
            try {
                for (Libro l : svcRecomendaciones.recomendar(idLibro)) {
                    modelo.addElement(l);
                }
            } catch (BibliotecaException ex) {
                mostrarError(ex);
            }
        }
        lstRecomendados.setModel(modelo);
    }

    // =====================================================================
    //  PUNTO DE ENTRADA
    // =====================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla, se queda con el aspecto por defecto
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
