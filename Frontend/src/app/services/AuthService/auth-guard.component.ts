import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { ApiAuthService } from './api-auth-service.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private authService: ApiAuthService, private router: Router) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        const isAdminRequired = route.data['isAdmin'];
        if (this.authService.isLoggedIn()) {
            if (isAdminRequired && this.authService.isAdmin()) {
                return true;
            } else if (!isAdminRequired) {
                return true;
            } else {
                this.router.navigate(['/']);
                return false;
            }
        } else {
            this.router.navigate(['/login']);
            return false;
        }
    }
}
