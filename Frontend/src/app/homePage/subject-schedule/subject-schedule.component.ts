import { Component } from '@angular/core';
import { SubjectScheduleResponse } from 'src/app/services/SubjectService/ResponseInfo/SubjectScheduleResponse';
import { SubjectServiceService } from 'src/app/services/SubjectService/subject-service.service';
import { CardInfo } from '../components/Card/card/CardInfo';

@Component({
  selector: 'app-subject-schedule',
  templateUrl: './subject-schedule.component.html',
  styleUrls: ['./subject-schedule.component.scss'],
})
export class SubjectScheduleComponent {

  getSubjectsForDay(day: string): { title: string; startHour: number; endHour: number; classRoom: String }[] {
    const dayIndex = this.days.indexOf(day) + 1;
    const subjectsForDay: { title: string; startHour: number; endHour: number; classRoom: String }[] = [];

    // Verifica que this.subjects sea un array
    if (!Array.isArray(this.subjects)) {
      console.error('this.subjects no es un array:', this.subjects);
      return []; // Retorna un array vacío en caso de error
    }

    for (const subject of this.subjects) {
      for (const sch of subject.schedule) {
        if (sch.dayOfWeek === dayIndex) {
          subjectsForDay.push({
            title: subject.title,
            startHour: sch.startHour,
            endHour: sch.endHour,
            classRoom: sch.classRoom
          });
        }
      }
    }

    return subjectsForDay; // Retorna las asignaturas para el día específico
  }

  subjects: SubjectScheduleResponse[] = [];
  days = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes'];
  hours = Array.from({ length: 16 }, (_, i) => `${7 + i}:00`);



  constructor(private subjectService: SubjectServiceService) {
    this.subjectService.getSchedule().subscribe(
      (data) => {
        this.subjects = data;
      },
      (error) => {
        console.error('Error al recibir la respuesta:', error);
      }
    );
  }


  getSubjectsForDayAndHour(day: string, hour: string): string {
    const hourNumber = parseInt(hour.split(':')[0], 10);

    // Verifica que this.subjects sea un array
    if (!Array.isArray(this.subjects)) {
      console.error('this.subjects no es un array:', this.subjects);
      return "error";
    }

    for (const subject of this.subjects) {
      for (const sch of subject.schedule) {
        if (
          sch.dayOfWeek === this.days.indexOf(day) + 1 &&
          sch.startHour <= hourNumber && sch.endHour > hourNumber
        ) {
          return subject.title
        }
      }
    }

    return ""; // Retorna vacío si no se encuentra ninguna asignatura
  }

  getCardInfo(schedule: { title: string; startHour: number; endHour: number; classRoom: String }): CardInfo {
    return new CardInfo(
      schedule.title,
      `${schedule.startHour}:00 - ${schedule.endHour}:00`,
      schedule.classRoom.toString(), // Convertir classRoom a string
      ""
    );
  }







}
