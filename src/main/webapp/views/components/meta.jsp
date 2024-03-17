<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
<style>
    .blurred::after {
        content: "";
        position: absolute;
        width: 100%;
        height: 100%;
        backdrop-filter: blur(10px);
        /* apply the blur */
        pointer-events: none;
        /* make the overlay click-through */
    }
    .background {
        position: absolute;
        width: 100%;
        height: 100vh;
        background-image: url('assets/bank.jpg');
        background-size: cover;
        z-index: -1;
    }
</style>