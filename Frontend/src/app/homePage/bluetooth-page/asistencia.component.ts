import { Component, OnInit } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { interval } from 'rxjs';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';
import { SubjectServiceService } from 'src/app/services/SubjectService/subject-service.service';
import { TeacherService } from 'src/app/services/TeacherService/teacher.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { Attendance } from 'src/app/services/UserService/Attendance';
import { SubjectInfo } from 'src/app/services/UserService/SubjectInfo';

@Component({
  selector: 'app-asistencia',
  templateUrl: './asistencia.component.html',
  styleUrls: ['./asistencia.component.scss'],
})
export class AsistenciaComponent implements OnInit {
  subjects: SubjectInfo[] = [];
  selectedSubjectId: number | null = null;
  attendanceCode: string = '';
  isLoading: boolean = false;
  attendances: Attendance[] = []
  showUsers: { [attendanceCode: string]: boolean } = {};

  ngOnInit() {
    this.getAsisttances()
    this.getAllSubjects();
  }

  constructor(
    private subjectService: SubjectServiceService,
    private apiUserService: ApiUserService,
    private teacherService: TeacherService,
    private apiAuthService: ApiAuthService,
    private toastController: ToastController
  ) {
    // Inicia un observable que actualiza los temporizadores cada segundo
    interval(1000).subscribe(() => {
      if (this.isTeacher()) {
        this.attendances.forEach(attendance => this.getTimeRemaining(attendance.dateTime));
      }
    });
  }

  toggleUsers(attendance: Attendance) {
    this.getAsisttances();
    this.showUsers[attendance.code] = !this.showUsers[attendance.code];
  }

  isTeacher(): boolean | undefined {
    return this.apiAuthService.isTeacher();
  }

  isLoggedIn(): boolean | undefined {
    return this.apiAuthService.isLoggedIn();
  }

  async presentToast(message: string, color: string) {
    const toast = await this.toastController.create({
      message,
      color,
      duration: 2000,
      position: 'top',
    });
    await toast.present();
  }

  createAttendance(): void {
    if (this.selectedSubjectId) {
      this.teacherService.createAttendance(this.selectedSubjectId).subscribe({
        next: () => { this.getAsisttances() },
        error: (err) => console.error('Error al crear la asistencia:', err),
      });
    } else {
      console.error('Debes seleccionar una materia.');
    }
  }

  joinAttendance(): void {
    if (this.attendanceCode) {
      this.apiUserService.joinAttendance(this.attendanceCode).subscribe({
        next: () => {
          this.presentToast('Registrado en la asistencia correctamente', 'success');
        },
        error: () => {
          this.presentToast('Error al registrarse en la asistencia', 'danger');
        },
      });
    } else {
      this.presentToast('Debes ingresar un código de asistencia', 'warning');
    }
  }

  getAsisttances() {
    this.teacherService.getAllAttendances().subscribe((attendances: Attendance[]) => { this.attendances = attendances })

  }

  getAllSubjects() {

    this.subjectService.getSubjects().subscribe((subjects: SubjectInfo[]) => { this.subjects = subjects })
  }

  addTimeToAttendance(attendanceId: number) {
    this.teacherService.addTimeToAttendance(attendanceId).subscribe({
      next: () => {
        this.presentToast('Tiempo añadido correctamente', 'success');
        this.getAsisttances(); // Actualizar la lista de asistencias
      },
      error: (error) => {
        console.error('Error al añadir tiempo:', error);
        this.presentToast('Error al añadir tiempo', 'danger');
      }
    });
  }

  getTimeRemaining(dateTime: Date): number {
    const FIVE_MINUTES_IN_MS = 5 * 60 * 1000; // 5 minutos en milisegundos
    const createdTime = new Date(dateTime).getTime(); // Fecha de creación en ms
    const currentTime = new Date().getTime(); // Hora actual en ms
    const timeElapsed = currentTime - createdTime; // Diferencia en ms
    const timeRemaining = FIVE_MINUTES_IN_MS - timeElapsed; // Tiempo restante

    return timeRemaining > 0 ? timeRemaining : 0; // Retorna 0 si ya expiró
  }

}