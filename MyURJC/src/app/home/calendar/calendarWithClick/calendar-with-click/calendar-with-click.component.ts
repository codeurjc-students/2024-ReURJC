import { Component, OnInit } from '@angular/core';
import { FestiveServiceService } from 'src/app/services/FestiveService/festive-service.service';
import { FestiveInfo } from 'src/app/services/FestiveService/FestiveInfo';

@Component({
  selector: 'app-calendar-with-click',
  templateUrl: './calendar-with-click.component.html',
  styleUrls: ['./calendar-with-click.component.scss'],
})
export class CalendarWithClickComponent  implements OnInit {

  month: { name: string; days: Date[] } = { name: '', days: [] }; // Objeto para el mes actual
  weekdays = ['L', 'M', 'X', 'J', 'V', 'S', 'D']; // Días de la semana
  festives: FestiveInfo[] = []; // Array de festivos

  constructor(private apiService: FestiveServiceService) { }

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

}
