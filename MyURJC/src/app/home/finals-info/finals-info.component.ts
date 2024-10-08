import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { SubjectInfo } from 'src/app/services/UserService/SubjectInfo';

@Component({
  selector: 'app-finals-info',
  templateUrl: './finals-info.component.html',
  styleUrls: ['./finals-info.component.scss'],
})
export class FinalsInfoComponent implements OnInit {
  // Variable para almacenar los datos recibidos
  subjects: SubjectInfo[] = [];

  constructor(private router: Router, private apiService: ApiUserService) {}

  ngOnInit() {
    this.apiService.getAll().subscribe({
      next: (data: SubjectInfo[]) => {
        // Ordenar las convocatorias de cada asignatura antes de asignar los datos
        this.subjects = data.map(subject => {
          const sortedConvocatories = subject.getConvocatory().sort((a, b) => {
            // Ordenar por el valor de convocatoria: 1 (septiembre), 2 (mayo), 3 (junio)
            return a.getConvocatory() - b.getConvocatory();
          });
          return new SubjectInfo(subject.getTitle(), sortedConvocatories);
        });
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  getConvocatoryMonth(convocatoryNumber: number): string {
    switch (convocatoryNumber) {
      case 1:
        return 'Septiembre';
      case 2:
        return 'Mayo';
      case 3:
        return 'Junio';
      default:
        return 'Desconocido';
    }
  }
}
