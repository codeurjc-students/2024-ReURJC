package com.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.Convocatory;
import com.example.model.Festive;
import com.example.model.News;
import com.example.model.Subject;
import com.example.model.User;
import com.example.repository.FestiveRepository;
import com.example.repository.NewsRepository;
import com.example.repository.SubjectRepository;
import com.example.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DataLoader {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private FestiveRepository festives;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private NewsRepository news;

    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        // Crear asignaturas de ejemplo
        Subject subject1 = new Subject(10L, "Historia");
        Subject subject2 = new Subject(20L, "Matemáticas");
        Subject subject3 = new Subject(30L, "Historia de la filología moderna");

        Convocatory conv1 = new Convocatory("10/11/2024", 1, "Aulario I, 2002"); //INGLÉS AMORE
        Convocatory conv2 = new Convocatory("10/9/2024", 2, "Aulario II, 2002");

        subject1.getConvocatories().add(conv1);
        subject1.getConvocatories().add(conv2);

        // Guardar asignaturas primero
        subjectRepository.saveAll(List.of(subject1, subject2, subject3));

        // Crear usuarios de ejemplo
        User user1 = new User(1L, "John", "Doe", "Smith", "12345678A","mariscalalonso16@icloud.com",passwordEncoder.encode("123"));
        User user2 = new User(2L, "Jane", "Doe", "Smith", "87654321B","yaovi@icloud.com",passwordEncoder.encode("123"));

        // Interconexión después de guardar las asignaturas
        user1.getSubjects().addAll(List.of(subject1, subject2));
        user2.getSubjects().add(subject3);

        //ROLES ASSIGN
        user1.setRoles(List.of("USER"));

        user2.setRoles(List.of("USER"));

        // Guardar usuarios
        userRepository.saveAll(List.of(user1, user2));

        Festive f2 = new Festive(4, 5, 2024,"#F24726");
        Festive f3 = new Festive(2, 1, 2024,"#33FF8C", "Alcorcón");
        Festive f4 = new Festive(3, 1, 2024, "#F24726", "Alcorcón");
        Festive f5 = new Festive(6, 1, 2025,"#33FF8C",14);
        festives.save(f2);
        festives.save(f3);
        festives.save(f4);
        festives.save(f5);

        News n1 = new News("Noticia sobre tecnología", 
    "Descubre los últimos avances en inteligencia artificial que están revolucionando industrias enteras. " +
    "Desde el aprendizaje automático hasta los algoritmos predictivos, la IA está creando nuevas oportunidades.", 
    "1/2/2024");

News n2 = new News("Partido de fútbol emocionante", 
    "En un partido lleno de emociones, el equipo local logró imponerse en el campeonato tras una tensa tanda de penales. " +
    "Los fanáticos celebran el triunfo en las calles mientras el equipo se prepara para la próxima temporada.", 
    "1/2/2024");

News n3 = new News("Nuevo gobierno toma posesión", 
    "Con un acto solemne, el nuevo gobierno ha asumido el poder. Entre sus principales prioridades están la " +
    "reforma económica y la mejora de la seguridad nacional. Se esperan cambios significativos en los próximos meses.", 
    "1/2/2024");

News n4 = new News("Estreno de película", 
    "La nueva superproducción de ciencia ficción ha llegado a los cines, con efectos visuales impresionantes y " +
    "una trama que mantiene a los espectadores al borde de sus asientos. Se espera que rompa récords de taquilla.", 
    "1/2/2024");

News n5 = new News("Descubrimiento científico", 
    "Astrónomos han encontrado un nuevo planeta en una zona habitable fuera de nuestro sistema solar. " +
    "Este descubrimiento abre nuevas posibilidades para la búsqueda de vida extraterrestre.", 
    "1/2/2024");

News n6 = new News("Crisis sanitaria mundial", 
    "La pandemia sigue afectando a gran parte del mundo. Los expertos en salud pública alertan sobre la necesidad " +
    "de medidas más estrictas para contener el brote, mientras los hospitales siguen operando a máxima capacidad.", 
    "1/2/2024");

News n7 = new News("Apertura de nuevo museo", 
    "Un nuevo museo de arte moderno ha abierto sus puertas en la ciudad, exhibiendo obras de artistas internacionales. " +
    "La inauguración ha atraído a cientos de personas interesadas en las nuevas tendencias artísticas.", 
    "1/2/2024");

News n8 = new News("Innovación en energías renovables", 
    "Investigadores han desarrollado una nueva tecnología que promete aumentar la eficiencia de los paneles solares, " +
    "haciendo que la energía renovable sea más accesible y barata para todos.", 
    "1/2/2024");

News n9 = new News("Competencia de atletismo", 
    "Los mejores atletas del mundo se dieron cita en el evento internacional de atletismo, donde se rompieron varios " +
    "récords en diversas categorías. Los espectadores vivieron un espectáculo inolvidable.", 
    "1/2/2024");

News n10 = new News("Reforma educativa", 
    "El gobierno ha presentado una nueva propuesta para reformar el sistema educativo, con énfasis en el uso de la " +
    "tecnología en las aulas y en mejorar la calidad de la enseñanza en áreas rurales.", 
    "1/2/2024");

News n11 = new News("Serie de televisión impactante", 
    "La última serie de suspenso ha capturado la atención de la audiencia con su historia intrigante y sus giros inesperados. " +
    "Los fanáticos están ansiosos por ver cómo se desenvuelve la trama en los próximos episodios.", 
    "1/2/2024");

News n12 = new News("Avance en la lucha contra el cáncer", 
    "Científicos han desarrollado un nuevo tratamiento que ha mostrado resultados prometedores en la lucha contra ciertos " +
    "tipos de cáncer, abriendo la puerta a tratamientos más efectivos en el futuro.", 
    "1/2/2024");

News n13 = new News("Vacuna para nueva enfermedad", 
    "La comunidad médica está celebrando el desarrollo de una vacuna para una nueva enfermedad viral que ha causado preocupación " +
    "en varias partes del mundo. Se espera que la producción en masa comience pronto.", 
    "1/2/2024");

News n14 = new News("Exposición de arte al aire libre", 
    "Una innovadora exposición de arte al aire libre está atrayendo a miles de personas. Las obras, repartidas por toda la ciudad, " +
    "ofrecen una experiencia visual única y un enfoque fresco sobre el arte contemporáneo.", 
    "1/2/2024");

News n15 = new News("Progreso en la colonización de Marte", 
    "Los avances en la tecnología espacial han permitido que las agencias espaciales den pasos importantes hacia la colonización " +
    "de Marte. Las próximas misiones planean establecer las primeras bases permanentes en el planeta rojo.", 
    "1/2/2024");

// Guardar en la base de datos
news.save(n1);
news.save(n2);
news.save(n3);
news.save(n4);
news.save(n5);
news.save(n6);
news.save(n7);
news.save(n8);
news.save(n9);
news.save(n10);
news.save(n11);
news.save(n12);
news.save(n13);
news.save(n14);
news.save(n15);

    }
}
