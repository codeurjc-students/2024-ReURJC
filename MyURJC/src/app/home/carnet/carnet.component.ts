import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';

@Component({
  selector: 'app-carnet',
  templateUrl: './carnet.component.html',
  styleUrls: ['./carnet.component.scss'],
})
export class CarnetComponent  implements OnInit {

  private carnetUrl: string | null = null;


  constructor(private userService: ApiUserService, private apiAuthService: ApiAuthService) { }

  ngOnInit(): void {
    // Suscribirse al observable que indica si el usuario está autenticado
    this.apiAuthService.loggedIn$.subscribe(loggedIn => {
  
      // Si el usuario está autenticado, obtener el carné
      if (this.getIsLoggedIn()) {
        this.userService.getUserCarnet().subscribe({
          next: (blob) => {
            // Crear una URL a partir del Blob del carné
            this.carnetUrl = URL.createObjectURL(blob);
          },
          error: (err) => {
            // Manejo de errores al intentar obtener el carné
            console.error('Error al obtener el carnet:', err);
          }
        });
      }
    });
  }
  

public getIsLoggedIn(): boolean {
  return this.apiAuthService.isLoggedIn();
}

public getCarnetUrl(): string | null {
  return this.carnetUrl;
}

}