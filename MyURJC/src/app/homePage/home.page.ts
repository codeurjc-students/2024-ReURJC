import { Component, OnInit, ViewChild } from '@angular/core';
import { IonContent, IonTab, IonTabs } from '@ionic/angular';
import { ActionPerformed, PushNotificationSchema, PushNotifications, Token } from '@capacitor/push-notifications';
import { ApiAuthService } from '../services/AuthService/api-auth-service.service';
import { CardInfo } from './components/Card/card/CardInfo';
import { EventServiceService } from '../services/EventService/event-service.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
})
export class HomePage implements OnInit{
  showNotificationBadge = false
  previousTab: string | undefined;
  servicesTabCards: CardInfo[] = [
    new CardInfo("Escolar","Asistencia Bluetooth","Este servicio permite confirmar tu asistencia a una clase",""),
    new CardInfo("Ocio","Reserva de cancha", "Este servicio permite realizar reservas de las pistas deportivas de la URJC","/reservations")
  ];

  schedulesTabCards: CardInfo[] = [
    new CardInfo("","Calendario","Consulta los días lectivos, vacaciones y festivos del curso académico actual","/calendar"),
    new CardInfo("","Horario", "Consulta tu horario académico diario y semanal","/schedule"),
    new CardInfo("","Exámenes finales","Consulta toda la información relacionada con los exámenes finales","/me/subjects")
  ];

  profileTabCards: CardInfo[] = [
    new CardInfo("","Calificaciones finales", "Consulta las notas finales de las asignaturas cursadas","/me/finalMarks")
  ];

  temporalEvents: { [tab: number]: CardInfo[] } = {  // Objeto para almacenar los eventos por pestaña
    1: [], 
    2: [],
    3: []
  };

  constructor(private apiAuthService: ApiAuthService, private eventService: EventServiceService) {
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



}
