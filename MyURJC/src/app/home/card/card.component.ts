import { Component, Input, OnInit } from '@angular/core';
import { CardInfo } from './CardInfo';
import { Router } from '@angular/router';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent  implements OnInit {
  private _services : CardInfo[] = []
  @Input() tab: number = 0;

  constructor(private router: Router) {}

  ngOnInit() {
    switch (this.tab) {
      
      case 1 : {
        this._services.push(new CardInfo("Escolar","Asistencia Bluetooth","Este servicio permite confirmar tu asistencia a una clase",""))
        this._services.push(new CardInfo("Ocio","Reserva de cancha", "Este servicio permite realizar reservas de las pistas deportivas de la URJC",""))
        this._services.push(new CardInfo("Escolar","Votaciones delegados","Este servicio permite inscribirte como delegado o votar cuando se abran las votaciones",""))

        break;

      }
      case 2 : {
        this._services.push(new CardInfo("","Calendario","Consulta los días lectivos, vacaciones y festivos del curso académico actual",""))
        this._services.push(new CardInfo("","Horario", "Consulta tu horario académico diario y semanal",""))
        this._services.push(new CardInfo("","Exámenes finales","Consulta toda la información relacionada con los exámenes finales","/me/subjects"))

        break;

      }

      case 3 : {
        this._services.push(new CardInfo("","Detalle tarjeta de estudiante","Consulta los detalle de tu tarjeta estudiantil",""))
        this._services.push(new CardInfo("","Calificaciones finales", "Consulta las notas finales de las asignaturas cursadas",""))
        
        break;

      }
    }
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
