import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { CardInfo } from './CardInfo';
import { ApiAuthService } from 'src/app/services/AuthService/api-auth-service.service';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})
export class CardComponent {

  @Input() isSubtitleFirst: boolean = false;
  @Input() card: CardInfo = new CardInfo('', '', '', '');

  constructor(private router: Router) { }

  navigate(path: string | undefined) {
    if (path !== undefined) {
      this.router.navigate([path]);
    }
  }
}
