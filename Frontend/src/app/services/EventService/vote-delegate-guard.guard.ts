import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { EventServiceService } from './event-service.service';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class voteDelegateGuard implements CanActivate {
  constructor(private eventService: EventServiceService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.eventService.isVoteDelegateActivated().pipe(
      map((data: boolean) => {
        if (data) {
          return true; // El evento está habilitado, permite la navegación
        } else {
          // El evento no está habilitado, redirige a otra página
          this.router.navigate(['/home']);
          return false;
        }
      }),
      catchError((error) => {
        console.error('Error en la verificación del evento', error);
        // En caso de error, se puede redirigir o devolver false
        this.router.navigate(['/home']);
        return [false];
      })
    );
  }
}
