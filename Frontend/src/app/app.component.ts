import { Component, OnInit } from '@angular/core';
import { ApiAuthService } from './services/AuthService/api-auth-service.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit{
  constructor(private apiAuth: ApiAuthService) { }

  ngOnInit(): void {
    this.apiAuth.reqIsLogged();
  }
}
