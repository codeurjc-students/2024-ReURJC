import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../UserService/user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiAuthService {
  apiUrl: string = "/api/login";
  
  private username: string = '';
  private user: User | undefined;
  private isLogged: boolean;

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      this.username = this.user?.email as string;
      this.isLogged = true;
    } else {
      this.isLogged = false;
    }
  }


  login(username: string, password: string) {
    this.username = username;
    const formData = { username: this.username, password: password };
    this.http.post("/api/users/login", formData, { withCredentials: true }).subscribe({
      next: (response: any) => { this.reqIsLogged() },
      error: (error: any) => { alert("Wrong credentials") }
    });
  }

  reqIsLogged() {
    this.http.get('/api/users/me', { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.user = response as User;
        this.isLogged = true;
        sessionStorage.setItem('user', JSON.stringify(this.user));
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        if (error.status != 404) {
          console.error('Error when asking if logged: ' + JSON.stringify(error));
        }
      }
    });
  }

  isLoggedIn(): boolean {
    return this.isLogged;
  }

  isAdmin() {
    return this.user && this.user.roles.indexOf('ADMIN') !== -1;
  }
}
