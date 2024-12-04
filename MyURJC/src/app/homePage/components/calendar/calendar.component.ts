import { Component, OnInit } from '@angular/core';
import { FestiveServiceService } from 'src/app/services/FestiveService/festive-service.service';
import { FestiveInfo } from 'src/app/services/FestiveService/FestiveInfo';
@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss'],
})
export class CalendarComponent implements OnInit {
  months: { name: string; days: Date[] }[] = []; // Array de meses con sus días
  weekdays = ['L', 'M', 'X', 'J', 'V', 'S', 'D']; // Días de la semana
  festives: FestiveInfo[] = []; // Array de festivos

  constructor(private apiService: FestiveServiceService) {}

  ngOnInit() {
    this.generateCalendar();
    this.loadFestives();
  }

  // Cargar festivos desde el servicio
  loadFestives() {
    this.apiService.getAllFestives().subscribe(
      (data) => {
        // Transformar los datos obtenidos en instancias de FestiveInfo
        this.festives = data
        console.log(this.festives)
  
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
  

  // Genera el calendario para todo el año
  generateCalendar() {
    let currentYear = new Date().getMonth() < 9 ? new Date().getFullYear() - 1 : new Date().getFullYear();
    
    // Generar meses de septiembre a septiembre
    for (let monthIndex = 8; monthIndex < 12; monthIndex++) {
      const daysInMonth = new Date(currentYear, monthIndex + 1, 0).getDate();
      const days: Date[] = [];
      for (let day = 1; day <= daysInMonth; day++) {
        days.push(new Date(currentYear, monthIndex, day));
      }
      this.months.push({
        name: this.getMonthName(monthIndex) + ' ' + currentYear,
        days: days,
      });
    }

    currentYear++; // Cambiar al siguiente año

    // Generar meses de enero a septiembre
    for (let monthIndex = 0; monthIndex < 9; monthIndex++) {
      const daysInMonth = new Date(currentYear, monthIndex + 1, 0).getDate();
      const days: Date[] = [];
      for (let day = 1; day <= daysInMonth; day++) {
        days.push(new Date(currentYear, monthIndex, day));
      }
      this.months.push({
        name: this.getMonthName(monthIndex) + ' ' + currentYear,
        days: days,
      });
    }
  }

  // Devuelve el nombre del mes
  getMonthName(monthIndex: number): string {
    const monthNames = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    return monthNames[monthIndex];
  }

  // Devuelve el día de la semana (0 para domingo, 1 para lunes, etc.)
  getDayOfWeek(day: Date) {
    return day.getDay();
  }

  // Verifica si el día actual es hoy
  isToday(date: Date): boolean {
    const today = new Date();
    return date.toDateString() === today.toDateString();
  }

  // Verifica si es fin de semana (sábado o domingo)
  isWeekend(date: Date): boolean {
    const day = date.getDay();
    return day === 6 || day === 0; // Sábado o domingo
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

  // Verifica si hay fiestas locales en el mes actual
  hasLocalFestives(month: { name: string; days: Date[] }): boolean {
    return this.festives.some((festive: FestiveInfo) => 
      month.days.some(day => 
        festive.day === day.getDate() && 
        festive.month === day.getMonth() + 1 && 
        festive.local !== null
      )
    );
  }

  // Obtiene las fiestas locales para el mes actual
  getLocalFestives(month: { name: string; days: Date[] }): FestiveInfo[] {
    return this.festives.filter((festive: FestiveInfo) => 
      month.days.some(day => 
        festive.day === day.getDate() && 
        festive.month === day.getMonth() + 1 && 
        festive.local !== null
      )
    );
  }
}
