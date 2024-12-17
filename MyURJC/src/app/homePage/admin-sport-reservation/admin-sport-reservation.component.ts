// admin-sport-reservation.component.ts
import { Component, OnInit } from '@angular/core';
import { AdminService } from 'src/app/services/AdminService/admin.service';
import { SportReservation } from 'src/app/services/UserService/SportReservation';

@Component({
  selector: 'app-admin-sport-reservation',
  templateUrl: './admin-sport-reservation.component.html',
  styleUrls: ['./admin-sport-reservation.component.scss'],
})
export class AdminSportReservationComponent implements OnInit {
  reservations: SportReservation[] = [];
  loading: boolean = true;
  pistas: { [key: number]: string } = {
    0: 'Pista de tenis (Alcorcón)',
    1: 'Pista de baloncesto (Móstoles)',
    2: 'Pista de fútbol (Móstoles)'
  };

  constructor(private adminService: AdminService) { } // Inyecta el servicio AdminService

  ngOnInit(): void {
    this.adminService.getAllReservations().subscribe(
      (reservations) => {
        this.reservations = reservations;
        this.loading = false;
      },
      (error) => {
        console.error('Error al obtener las reservas:', error);
        this.loading = false;
        // Aquí puedes manejar el error, por ejemplo, mostrando un mensaje al usuario
      }
    );



    
  }

  traducirPista(numeroPista: number): string {
    return this.pistas[numeroPista] || 'Pista desconocida';
  }
}