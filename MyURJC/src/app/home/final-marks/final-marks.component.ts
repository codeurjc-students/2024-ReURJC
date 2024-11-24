import { Component, OnInit } from '@angular/core';
import { Socket } from 'ngx-socket-io';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { SubjectMark } from 'src/app/services/UserService/SubjectMark';

@Component({
  selector: 'app-final-marks',
  templateUrl: './final-marks.component.html',
  styleUrls: ['./final-marks.component.scss'],
})
export class FinalMarksComponent  implements OnInit {

  grades:   
 { [subjectTitle: string]: { [convocatory: string]: SubjectMark[] } } = {};

  constructor(private apiUserService: ApiUserService) { 
    
    
  }

  ngOnInit() {


    this.apiUserService.getGrades().subscribe(
      (grades: SubjectMark[]) => {
        this.processGrades(grades);
      },
      (error) => {
        console.error('Error al obtener las notas', error);
      }
    );
  }

  private processGrades(grades: SubjectMark[]) {
    for (const grade of grades) {
      const subjectTitle = grade.subjectId.title; // Usa el título de la asignatura como clave
      if (!this.grades[subjectTitle]) {
        this.grades[subjectTitle] = {};
      }
      if (!this.grades[subjectTitle][grade.convocatory]) {
        this.grades[subjectTitle][grade.convocatory] = [];
      }
      this.grades[subjectTitle][grade.convocatory].push(grade);
    }
  }

  getSubjectTitles(): string[] {
    return Object.keys(this.grades);
  }

  getConvocatories(subjectTitle: string): string[] {
    return Object.keys(this.grades[subjectTitle]);
  }

}
