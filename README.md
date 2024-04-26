
![Shark Nice](https://media3.giphy.com/media/QHsDPTyYU2R0Y/giphy.gif?cid=47028fa8xr94v2susupwyzzqea2mkstn3h69sb0hmzcnj8i7&ep=v1_gifs&rid=giphy.gif&ct=g)
![Keanu Thumb](https://media2.giphy.com/media/uiMIJMFYgRaAz5Pcb7/giphy.gif?cid=47028fa8xr94v2susupwyzzqea2mkstn3h69sb0hmzcnj8i7&ep=v1_gifs&rid=giphy.gif&ct=g)
![Schitts Creek Perfect](https://media2.giphy.com/media/KEYbcgR8oKQzwpwvLU/giphy-downsized-medium.gif?cid=47028fa8xr94v2susupwyzzqea2mkstn3h69sb0hmzcnj8i7&ep=v1_gifs&rid=giphy-downsized-medium.gif&ct=g)
![Japanese OK](https://media0.giphy.com/media/26gQt4Oqpz2JshYis/giphy.gif?cid=47028fa8xr94v2susupwyzzqea2mkstn3h69sb0hmzcnj8i7&ep=v1_gifs&rid=giphy.gif&ct=g)
![Biden Smile](https://media3.giphy.com/media/dAvvMX8BLOvCg/giphy.gif?cid=47028fa8xr94v2susupwyzzqea2mkstn3h69sb0hmzcnj8i7&ep=v1_gifs&rid=giphy.gif&ct=g)


backend folder contains the java backend
the root dir is a react vite app that can be lauched via `pnpm run dev`

`/hotel` page on the frontend produces a code that will be shown to the traveller that is than scanned by them. This code is only know to the backend server and the receiving hotel, thus scanning it constitutes an additional factor to prove that you stayed at that hotel. 

that code is than send to backend and will, if the code matches the data of the record of the backend and the provided publickey is kyc verified, a NFT is minted to that address related to the publickey.

Currently there is the issue that anyone could mint nfts for every kyc verified wallet but that could be solved by attaching a signature to the request. This was left out due to time reasons.

The backend will then resond with either 200 if everything checked out or 412 when the code was expired or the wallet is not kyc verified.

The 200 response will include the transactionID of the sending of the NFT including an auth-token that can also be used to verify that the corresponding code was seen by the traverller. The endpoint is on the backend. This exists so to ease the implementation of traditional processes.