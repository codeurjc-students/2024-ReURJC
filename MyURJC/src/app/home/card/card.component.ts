import { Component, Input, OnInit } from '@angular/core';
import { CardInfo } from './CardInfo';
import { Router } from '@angular/router';
import {EventServiceService } from 'src/app/services/EventService/event-service.service';
import { CardEvent } from 'src/app/services/EventService/cardEvent';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent  implements OnInit {
  private _services : CardInfo[] = []
  @Input() tab: number = 0;

  constructor(private router: Router, private eventService: EventServiceService) {}

  ngOnInit() {

    
    switch (this.tab) {
      
      case 1 : {
        this._services.push(new CardInfo("Escolar","Asistencia Bluetooth","Este servicio permite confirmar tu asistencia a una clase",""))
        this._services.push(new CardInfo("Ocio","Reserva de cancha", "Este servicio permite realizar reservas de las pistas deportivas de la URJC","/reservations"))
        
        break;

      }
      case 2 : {
        this._services.push(new CardInfo("","Calendario","Consulta los días lectivos, vacaciones y festivos del curso académico actual","/calendar"))
        this._services.push(new CardInfo("","Horario", "Consulta tu horario académico diario y semanal","/schedule"))
        this._services.push(new CardInfo("","Exámenes finales","Consulta toda la información relacionada con los exámenes finales","/me/subjects"))

        break;

      }

      case 3 : {
        this._services.push(new CardInfo("","Detalle tarjeta de estudiante","Consulta los detalle de tu tarjeta estudiantil",""))
        this._services.push(new CardInfo("","Calificaciones finales", "Consulta las notas finales de las asignaturas cursadas","/me/finalMarks"))

        break;

      }
    }
    this.chargeTemporalEvents(this.tab)
  }
  chargeTemporalEvents(tab: number) {
    this.eventService.getAllEvents().subscribe(eventArray => {
      eventArray.forEach(eventE => {
        if (eventE.tabDisplay === tab) {
          this._services.push(eventE);
        }
      });
    });
    
  }
  

  get services(): CardInfo[] {
    return this._services;
  }

  navigate(path: string | undefined) {
    if (path !== undefined) {
    this.router.navigate([path])

    }
  }

  


}
