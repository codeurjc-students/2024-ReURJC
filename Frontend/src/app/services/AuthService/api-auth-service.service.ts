import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../UserService/user.model';

@Injectable({
  providedIn: 'root'
})
export class ApiAuthService {
  private readonly API_BASE_URL = '/api/v1/users';
  private username: string = '';
  private user: User | undefined;
  private loggedIn = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedIn.asObservable();
  public fcmToken: string = "";

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = sessionStorage.getItem('user');
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      this.username = this.user?.email as string;
      this.loggedIn.next(true);
    } else {
      this.loggedIn.next(false);
    }
  }

  login(username: string, password: string) {
    this.username = username;
    const formData = { username: this.username, password: password };
    this.http.post(`${this.API_BASE_URL}/login`, formData, { withCredentials: true })
      .subscribe({
        next: (response: any) => { this.fcmToken == "" ? this.reqIsLogged() : this.sendToken(this.fcmToken) },
        error: (error: any) => { alert("Wrong credentials") }
      });
  }

  sendToken(token: string) {
    this.http.post<any>(`${this.API_BASE_URL}/setDeviceToken?fcmToken=${token}`, {}, { withCredentials: true })
      .subscribe({
        next: (response: any) => { this.reqIsLogged() },
        error: (error: any) => { alert("Wrong credentials") }
      });
  }

  reqIsLogged() {
    this.http.get(`${this.API_BASE_URL}/me`, { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.user = response as User;
        this.loggedIn.next(true);
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
    return this.loggedIn.getValue();
  }

  isAdmin() {
    return this.user && this.user.roles.indexOf('ADMIN') !== -1;
  }

  isTeacher() {
    return this.user && this.user.roles.indexOf('TEACHER') !== -1;
  }

  getUser(): User | undefined {
    return this.user;
  }
}