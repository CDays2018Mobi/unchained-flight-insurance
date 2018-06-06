import {Injectable} from '@angular/core';
import {MessageService} from "primeng/components/common/messageservice";

@Injectable()
export class NotifierService {

  constructor(private messageService: MessageService) {
  }

  success(title: string, key?: string) {
    this.message('success', title, key);
  }

  info(title: string, key?: string) {
    this.message('info', title, key);
  }

  failure(title: string, key?: string) {
    this.message('error', title, key);
  }

  private message(severity: string = 'info', title: string, key: string = '') {
    this.messageService.add({severity: severity, summary: title, detail: '', key: key});

    setTimeout(() => {
      this.messageService.clear();
    }, 2000);
  }

}
