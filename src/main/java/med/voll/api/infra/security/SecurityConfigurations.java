package med.voll.api.infra.security;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration//Anotacion para indicarle a Spring que escane esta clase como una configuracion.
@EnableWebSecurity //Anotacion indica a spring que habilite el modo web security para esta clase de configuracion.
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter; //inyectar Mi filtro SecurityFilter
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {

        //El csrf - Cross-site request forgery y método de protection. Esto es para evitar suplantación de identidad
        //El csrf lo desabilito ya que no lo necesito porque ya tengo autenticación vía token o mi autenticación y autorización va ser vía web token no tengo por qué tener una protección de Cross-site request forgery,
        //La autenticación stateless ya me protege contra ese tipo de ataques, "Suplantacion de identidad".

        return httpSecurity.csrf(csrf->csrf.disable())
                .sessionManagement((sess-> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))) //le indicamos a spring el tipo de sesion
                .authorizeHttpRequests((request -> request.requestMatchers(HttpMethod.POST,"/login")
                        .permitAll()
                        .anyRequest()
                        .authenticated()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)//agrega mi filtro antes y en UsernamePasswordAuthenticationFilter valia que el usuario que esta iniciando la sesion existe y ya esta autenticado.
         .build();

    }//decirle Spring, la política de creación es stateless y cada request que haga match que es un request de tipo post y va para login permitirle a todos. Después todos los requests tienen que ser autenticados. Y bueno, construye el objeto finalmente.


   // con el metodo SecurityFilerChain la autenticación ya es stateless, ya no maneja por defecto una autenticación a nivel de Spring Security, que me cierra todo por defecto ybque da una página de login por defecto autogenerada.
 //Sino que ahora yo tengo el control sobre mí autenticación,

    /*
    //Metodo recomendado por el instructor
    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, "/login").permitAll()
            .anyRequest().authenticated()
            .and().build();
}

     */


        /*//No funciono
        security.csrf(csrf->csrf.disable())
                .sessionManagement((sess-> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests((request -> request.requestMatchers(HttpMethod.POST,"/login")
                        .permitAll()
                        .anyRequest().authenticated()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
        */

    /*//No funciono
      return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement((session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))) // Le indicamos a spring el tipo de sesion, la politica de creacion es STATELESS
                .authorizeHttpRequests((request -> request.requestMatchers(HttpMethod.POST, "/login") // Cada request que haga match del tipo post y va para el login
                        .permitAll() // Concede todos los permisos
                        .anyRequest() // Los request que vayan despues
                        .authenticated() // Debe ser autenticados
                )).build(); // Al final construye el objeto
     */


    //Objeto de authenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}



















