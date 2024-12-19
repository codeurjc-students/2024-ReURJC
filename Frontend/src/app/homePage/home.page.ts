import { Component, OnInit } from '@angular/core';
import { ActionPerformed, PushNotificationSchema, PushNotifications, Token } from '@capacitor/push-notifications';
import { ApiAuthService } from '../services/AuthService/api-auth-service.service';
import { CardInfo } from './components/Card/card/CardInfo';
import { EventServiceService } from '../services/EventService/event-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit {

  showNotificationBadge = false
  previousTab: string | undefined;
  servicesTabCards: CardInfo[] = [
    new CardInfo("Escolar", "Asistencia Bluetooth", "Este servicio permite confirmar tu asistencia a una clase", "/asistencia", [1]),
    new CardInfo("Ocio", "Reserva de cancha", "Este servicio permite realizar reservas de las pistas deportivas de la URJC", "/reservations", [1, 2]),
    new CardInfo("Ocio", "Reservas de cancha", "Este servicio permite revisar las reservas activas hechas por los usuarios", "/admin/reservationEvents", [3]),
    new CardInfo("Escolar", "Eventos de votos", "Este servicio permite revisar los votos de los usuarios en los eventos", "/admin/adminEvents", [3]),
    new CardInfo("Escolar", "Crear asistencia", "Este servicio permite crear une vento de asistencia", "/asistencia", [2])
  ];

  schedulesTabCards: CardInfo[] = [
    new CardInfo("", "Calendario", "Consulta los días lectivos, vacaciones y festivos del curso académico actual", "/calendar", [0, 1, 2, 3]),
    new CardInfo("", "Horario", "Consulta tu horario académico diario y semanal", "/schedule", [1, 2]),
    new CardInfo("", "Exámenes finales", "Consulta toda la información relacionada con los exámenes finales", "/me/subjects", [1])
  ];

  profileTabCards: CardInfo[] = [
    new CardInfo("", "Calificaciones finales", "Consulta las notas finales de las asignaturas cursadas", "/me/finalMarks", [1])
  ];

  temporalEvents: { [tab: number]: CardInfo[] } = {  // Objeto para almacenar los eventos por pestaña
    1: [],
    2: [],
    3: []
  };

  constructor(private router: Router, private apiAuthService: ApiAuthService, private eventService: EventServiceService) {
    this.chargeTemporalEvents();
  }

  ngOnInit() {
    PushNotifications.requestPermissions().then((result) => {
      if (result.receive === 'granted') {
        PushNotifications.register();
      } else {
      }

    });

    PushNotifications.addListener('registration', (token: Token) => {
      this.apiAuthService.fcmToken = token.value;
    });

    PushNotifications.addListener('registrationError', (error: any) => {
      alert('Error on registration: ' + JSON.stringify(error));
    });

    PushNotifications.addListener('pushNotificationReceived', (notification: PushNotificationSchema) => {
    });

    PushNotifications.addListener('pushNotificationActionPerformed', (notification: ActionPerformed) => {
    });
  }
  chargeTemporalEvents() {
    this.eventService.getAllEvents().subscribe(eventArray => {
      eventArray.forEach(eventE => {
        const tab = eventE.tabDisplay;
        if (this.temporalEvents[tab]) { // Verificar si la pestaña existe en el objeto
          this.temporalEvents[tab].push(eventE);
        }
      });
    });
  }

  onTabChange(event: any) {
    if (this.previousTab === 'alerts' && event.tab !== 'alerts') {
      // Se acaba de salir de la pestaña "alerts"
      this.showNotificationBadge = false;
    }
    this.previousTab = event.tab;
  }

  hideNotificationBadge() {
    this.showNotificationBadge = false;
  }

  onScroll(event: any) {
    this.showNotificationBadge = false;
  }

  canShowCard(card: CardInfo): boolean | undefined {
    if (this.isLoggedIn()) {
      if (this.isAdmin()) {
        return card.available?.includes(3)

      } else {
        if (this.isTeacher()) {
          return card.available?.includes(2)
        } else {
          return card.available?.includes(1)
        }
      }

    } else {
      return card.available?.includes(0);
    }

  }

  isTeacher(): boolean | undefined {
    return this.apiAuthService.isTeacher();
  }

  isAdmin(): boolean | undefined {
    return this.apiAuthService.isAdmin();
  }



  isLoggedIn(): boolean | undefined {
    return this.apiAuthService.getUser() !== undefined;
  }

  login() {
    this.router.navigate(['/login']);
  }




}
