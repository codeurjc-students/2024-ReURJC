import { Component, OnInit } from '@angular/core';
import { FestiveServiceService } from 'src/app/services/FestiveService/festive-service.service';
import { FestiveInfo } from 'src/app/services/FestiveService/FestiveInfo';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { SportReservation } from 'src/app/services/UserService/SportReservation';

@Component({
  selector: 'app-calendar-with-click',
  templateUrl: './calendar-with-click.component.html',
  styleUrls: ['./calendar-with-click.component.scss'],
})
export class CalendarWithClickComponent implements OnInit {

  month: { name: string; days: Date[] } = { name: '', days: [] }; // Objeto para el mes actual
  weekdays = ['L', 'M', 'X', 'J', 'V', 'S', 'D']; // Días de la semana
  festives: FestiveInfo[] = []; // Array de festivos
  tituloPista: string = 'Pistas';
  horas: number[] = [];

  pistaActiva: boolean = true; // La primera columna siempre inicia activa
  calendarioActivo: boolean = false;
  horasActivo: boolean = false;
  continuacion: string | undefined;
  tieneReserva: boolean = false;
  dayChoosen: Date | undefined;
  reservation: SportReservation | undefined;

  constructor(private apiService: FestiveServiceService, private apiUserservice: ApiUserService) { }

  ngOnInit() {
    this.generateCalendar();
    this.loadFestives();
    this.hasAReservation();
    this.obtenerReserva();
  }

  // Cargar festivos desde el servicio
  loadFestives() {
    this.apiService.getAllFestives().subscribe(
      (data) => {
        // Transformar los datos obtenidos en instancias de FestiveInfo
        this.festives = data

        const festiveRange: FestiveInfo[] = this.festives.reduce((acc: FestiveInfo[], festive: FestiveInfo) => {
          if (festive.startedXDaysAgo !== -1) {
            return acc.concat(FestiveInfo.generateFestivesFromRange(festive));
          }
          return acc;
        }, []);

        // Concatenar festivos originales con el rango generado
        this.festives = this.festives.concat(festiveRange);
      },
      (error) => {
        console.error('Error al recibir la respuesta:', error);
      }
    );
  }
  generateCalendar() {
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth();

    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate(); 1
    const days: Date[] = [];
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(currentYear, currentMonth, day));
    }
    this.month = { // Asignar los valores al objeto month
      name: this.getMonthName(currentMonth) + ' ' + currentYear,
      days: days,
    };
  }
  generateHourButtons(reservas: any[]): number[] {
    const hours = [];
    for (let i = 9; i <= 21; i += 2) {
      // Verificar si la hora ya está reservada
      const horaReservada = reservas.some(reserva => {
        const fechaReserva = new Date(reserva.date);
        return fechaReserva.getHours() === i && fechaReserva.getDate() === this.dayChoosen?.getDate();
      });

      if (!horaReservada) {
        hours.push(i);
      }
    }
    return hours;
  }

  getMonthName(monthIndex: number): string {
    const monthNames = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    return monthNames[monthIndex];
  }

  getDayOfWeek(day: Date) {
    return day.getDay();
  }

  // Verifica si el día actual es hoy
  isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear();
  }

  // Verifica si es fin de semana (sábado o domingo)
  isPreviousDay(date: Date): boolean {
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Establece la hora de "hoy" a medianoche para comparar solo las fechas
    date.setHours(0, 0, 0, 0); // Lo mismo para la fecha que se recibe

    return date <= today;
  }

  // Verifica si el día es festivo
  isFestive(date: Date): boolean {
    return this.festives.some((festive: FestiveInfo) =>
      festive.day === date.getDate() &&
      festive.month === date.getMonth() + 1 &&
      festive.year === date.getFullYear()
    );
  }

  // Aplica color personalizado a los días festivos
  applyCustomColor(date: Date): string {
    const festive = this.festives.find((festive: FestiveInfo) =>
      festive.day === date.getDate() &&
      festive.month === date.getMonth() + 1 &&
      festive.year === date.getFullYear()
    );

    return festive ? festive.color : ''; // Devuelve el color o vacío
  }


  activarCalendario(pista: string) {
    this.tituloPista = `Pista: ${pista}`;
    this.calendarioActivo = true;
    this.horasActivo = false; // Reiniciar la selección de horas
    this.dayChoosen = undefined; // Reiniciar el día seleccionado
  }

  activarHoras(day: Date) {
    if (!this.isPreviousDay(day)) {
      this.continuacion = "para el " + day.getDate().toString() + "/" + day.getMonth();
      this.dayChoosen = day;

      // Obtener el número de pista
      let pista = 0;
      if (this.tituloPista.includes('Tenis')) {
        pista = 0;
      } else if (this.tituloPista.includes('Baloncesto')) {
        pista = 1;
      } else if (this.tituloPista.includes('Fútbol')) {
        pista = 2;
      }

      // Llamar al servicio para obtener las reservas
      const reservationInfo = {
        año: day.getFullYear(),
        mes: day.getMonth() + 1, // Los meses en JavaScript van de 0 a 11
        dia: day.getDate()
      };

      this.apiUserservice.getSportReservations(pista, reservationInfo).subscribe(
        (reservas) => {
          this.horas = this.generateHourButtons(reservas); // Generar las horas disponibles
          this.horasActivo = true; // Mostrar las horas
        },
        (error) => {
          console.error('Error al obtener las reservas:', error);
        }
      );
    }
  }

  hasAReservation() {
    this.apiUserservice.reqIsUserWithReservation().subscribe(data => {
      this.tieneReserva = data; // Actualizar la variable con la respuesta de la API

    });
  }

  cancelarReserva() {
    this.apiUserservice.cancelReservation().subscribe(data => { this.hasAReservation(); });


  }


  reservar(hour: number) {
    if (this.dayChoosen) {
      const formattedHour = hour < 10 ? `0${hour}` : `${hour}`; // Añadir un cero a la izquierda si es necesario
      let pista = 0; // Valor por defecto

      // Determinar el valor de 'pista' según this.tituloPista
      if (this.tituloPista.includes('Tenis')) {
        pista = 0;
      } else if (this.tituloPista.includes('Baloncesto')) {
        pista = 1;
      } else if (this.tituloPista.includes('Fútbol')) {
        pista = 2;
      }
      const reservationData = {
        año: this.dayChoosen.getFullYear(),
        mes: this.dayChoosen.getMonth() + 1,
        fecha: this.dayChoosen.getDate(),
        hora: formattedHour,
        pista: pista
      };

      this.apiUserservice.newSportReservation(reservationData).subscribe(
        (response) => {
            this.hasAReservation()
            this.obtenerReserva();
        }
      );
    }
  }

  obtenerReserva() {
    this.apiUserservice.getMyReservation().subscribe(
      (reservation: SportReservation) => {
        this.reservation = reservation;
        if (reservation.pista === 0) {
          this.tituloPista = 'Tenis (Alcorcón)';
        } else if (reservation.pista === 1) {
          this.tituloPista = 'Baloncesto (Móstoles)';
        } else if (reservation.pista === 2) {
          this.tituloPista = 'Fútbol (Móstoles)';
        }
      },
      (error) => {
        console.error('Error al obtener la reserva:', error);
      }
    );
  }

  onPress(event: Event): void {
    const target = event.target as HTMLElement;
    target.classList.add('pressed');
  }

  onRelease(event: Event): void {
    const target = event.target as HTMLElement;
    target.classList.remove('pressed');
  }


}
