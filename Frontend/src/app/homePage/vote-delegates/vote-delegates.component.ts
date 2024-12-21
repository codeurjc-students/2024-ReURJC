import { Component, OnInit } from '@angular/core';
import { EventServiceService } from 'src/app/services/EventService/event-service.service';
import { ApiUserService } from 'src/app/services/UserService/api.user.service';
import { User } from 'src/app/services/UserService/user.model';
import { CardInfo } from '../components/Card/card/CardInfo';

@Component({
  selector: 'app-vote-delegates',
  templateUrl: './vote-delegates.component.html',
  styleUrls: ['./vote-delegates.component.scss'],
})
export class VoteDelegatesComponent implements OnInit {

  candidates: User[] = []
  private userVoted: boolean = false

  constructor(private userService: ApiUserService) { }

  ngOnInit() {
    this.userService.getCandidates().subscribe(response => {
      this.candidates = response;
    })

    this.userService.hasVoted().subscribe(data => { this.userVoted = data }
    )
  }

  getCardInfo(candidate: User): CardInfo {
    return new CardInfo(
      `${candidate.name} ${candidate.surname1} ${candidate.surname2}`,
      "", // No necesitas subtítulo en este caso
      "", // No necesitas descripción en este caso
      ""  // No necesitas apiCaller en este caso
    );
  }

  voteForCandidate(candidate: number) {
    this.userService.vote(candidate).subscribe((data) => {
      this.userService.hasVoted().subscribe((status) => { this.userVoted = status })
    })

  }

  getUserVoted(): boolean {
    return this.userVoted;
  }


}
