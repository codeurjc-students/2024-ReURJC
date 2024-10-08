import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiAuthService } from '../services/AuthService/api-auth-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {

  username: string = '';
  password: string = '';

  constructor(private apiService: ApiAuthService, private router: Router) { }

  onSubmit() {
    if (this.username !== '' && this.password !== '') {
      this.apiService.login(this.username, this.password);
    }
  }

}
